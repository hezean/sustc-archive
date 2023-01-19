module top_module ();
	reg clk=0;
	always #100 clk = ~clk;  // Create clock with period=10
	initial `probe_start;   // Start the timing diagram

	`probe(clk);        // Probe signal "clk"

	// A testbench
    reg[31:0]  Read_data_1;		// 从译码单元的Read_data_1中来
    reg[31:0]  Read_data_2;		// 从译码单元的Read_data_2中来
    reg[31:0]  Sign_extend;		// 从译码单元来的扩展后的立即数
    reg[5:0]   Function_opcode;  	// 取指单元来的r-类型指令功能码,r-form instructions[5:0]
    reg[5:0]   Exe_opcode;  		// 取指单元来的操作码
    reg[1:0]   ALUOp;             // 来自控制单元的运算指令控制编码
    reg[4:0]   Shamt;             // 来自取指单元的instruction[10:6]，指定移位次数
    reg  	   Sftmd;            // 来自控制单元的，表明是移位指令
    reg        ALUSrc;            // 来自控制单元，表明第二个操作数是立即数（beq，bne除外）
    reg        I_format;          // 来自控制单元，表明是除beq, bne, LW, SW之外的I-类型指令
    reg        Jr;               // 来自控制单元，表明是JR指令
    reg[31:0]  PC_plus_4;         // 来自取指单元的PC+4
    wire       Zero;              // 为1表明计算值为0 
    reg[31:0] ALU_Result;        // 计算的数据结果
    wire[31:0] Addr_Result;		// 计算的地址结果
    
    `probe(Zero);
    `probe(ALU_Result);
    `probe(Addr_Result);
    
    executs32 e(Read_data_1,Read_data_2,Sign_extend,Function_opcode,Exe_opcode,ALUOp,
                 Shamt,ALUSrc,I_format,Zero,Jr,Sftmd,ALU_Result,Addr_Result,PC_plus_4
                 );
	initial begin
		#0
        I_format = 0;
        Exe_opcode = 6'b0;
        ALUOp = 2'b10;
        Function_opcode = 6'b100000;
        Shamt = 5'b0;
        Sftmd = 0;
        ALUSrc = 0;
        Jr = 0;
        PC_plus_4 = 32'b0;
        Read_data_1 = 'd5;
        Read_data_2 = 'd6;
        Sign_extend = 32'b0;
        
		#200 
        I_format = 1;
        Exe_opcode = 6'b0;
        ALUOp = 2'b10;
        Function_opcode = 6'b100000;
        Shamt = 5'b0;
        Sftmd = 0;
        ALUSrc = 1;
        Jr = 0;
        PC_plus_4 = 32'b0;
        Read_data_1 = 'hffff_ff40;
        Read_data_2 = 'd6;
        Sign_extend = 'h3;
        
		#400 
        I_format = 0;
        Exe_opcode = 'b0100;
        ALUOp = 2'b10;
        Function_opcode = 6'b10_0100;
        Shamt = 5'b0;
        Sftmd = 0;
        ALUSrc = 0;
        Jr = 0;
        PC_plus_4 = 32'b0;
        Read_data_1 = 'h0000_00ff;
        Read_data_2 = 'h0000_0ff0;
        Sign_extend = 32'b0;
        
		#600 
        I_format = 0;
        Exe_opcode = 6'b0;
        ALUOp = 2'b0;
        Function_opcode = 6'b000000;
        Shamt = 'h3;
        Sftmd = 1;
        ALUSrc = 1;
        Jr = 0;
        PC_plus_4 = 32'b0;
        Read_data_1 = 'h2;
        Read_data_2 = 'h3;
        Sign_extend = 'h3;
        
		
		#50 $finish;            // Quit the simulation
	end
endmodule

module executs32(Read_data_1,Read_data_2,Sign_extend,Function_opcode,Exe_opcode,ALUOp,
                 Shamt,ALUSrc,I_format,Zero,Jr,Sftmd,ALU_Result,Addr_Result,PC_plus_4
                 );
    input[31:0]  Read_data_1;		// 从译码单元的Read_data_1中来
    input[31:0]  Read_data_2;		// 从译码单元的Read_data_2中来
    input[31:0]  Sign_extend;		// 从译码单元来的扩展后的立即数
    input[5:0]   Function_opcode;  	// 取指单元来的r-类型指令功能码,r-form instructions[5:0]
    input[5:0]   Exe_opcode;  		// 取指单元来的操作码
    input[1:0]   ALUOp;             // 来自控制单元的运算指令控制编码
    input[4:0]   Shamt;             // 来自取指单元的instruction[10:6]，指定移位次数
    input  		 Sftmd;            // 来自控制单元的，表明是移位指令
    input        ALUSrc;            // 来自控制单元，表明第二个操作数是立即数（beq，bne除外）
    input        I_format;          // 来自控制单元，表明是除beq, bne, LW, SW之外的I-类型指令
    input        Jr;               // 来自控制单元，表明是JR指令
    input[31:0]  PC_plus_4;         // 来自取指单元的PC+4
    output       Zero;              // 为1表明计算值为0 
    output reg[31:0] ALU_Result;        // 计算的数据结果
    output[31:0] Addr_Result;		// 计算的地址结果        
    

    wire[31:0] Ainput,Binput;       // two operands for calculation
    wire[5:0] Exe_code;             // use to generate ALU_ctrl. (I_format==0) ? Function_opcode : { 3'b000 , Opcode[2:0] };
    wire[2:0] ALU_ctl;              // the control signals which affact operation in ALU directely
    wire[2:0] Sftm;                 // identify the types of shift instruction, equals to Function_opcode[2:0]
    reg[31:0] Shift_Result;         // the result of shift operation
    reg[31:0] ALU_output_mux;
    wire[32:0] Branch_Addr;         // the calculated address of the instruction, Addr_Result is Branch_Addr[31:0]

    assign Ainput = Read_data_1;
    assign Binput = (ALUSrc == 0) ? Read_data_2 : Sign_extend[31:0];    //Immediate or not
    
    assign Exe_code = (I_format == 1'b0) ? Function_opcode : { 3'b000 , Exe_opcode[2:0] };
    assign ALU_ctl[0] = (Exe_code[0] | Exe_code[3]) & ALUOp[1];
    assign ALU_ctl[1] = ((!Exe_code[2]) | (!ALUOp[1]));
    assign ALU_ctl[2] = (Exe_code[1] & ALUOp[1]) | ALUOp[0];

    assign Zero = ALU_output_mux == 32'd0 ? 1'b1 : 1'b0;
    assign Branch_Addr = PC_plus_4[31:2] + Sign_extend;
    assign Addr_Result = Branch_Addr;

    always @(ALU_ctl or Ainput or Binput) begin
        case (ALU_ctl)
            3'b000: ALU_output_mux =  Ainput & Binput;
            3'b001: ALU_output_mux =  Ainput | Binput;
            3'b010: ALU_output_mux =  Ainput + Binput;
            3'b011: ALU_output_mux =  Ainput + Binput;
            3'b100: ALU_output_mux =  Ainput ^ Binput;
            3'b101: ALU_output_mux =  ~(Ainput | Binput);
            3'b110: ALU_output_mux =  Ainput - Binput;
            3'b111: ALU_output_mux =  Ainput - Binput;
            default: ALU_output_mux = 32'h00000000;
        endcase    
    end

    always @* begin
        if(Sftmd)
            case (Sftm[2:0])
                3'b000: Shift_Result = Binput << Shamt;     //Sll rd,rt,shamt 
                3'b010: Shift_Result = Binput >> Shamt;     //Srl rd,rt,shamt
                3'b100: Shift_Result = Binput << Ainput;    //Sllv rd,rt,rs
                3'b110: Shift_Result = Binput >> Ainput;    //Srlv rd,rt,rs
                3'b011: Shift_Result = $signed(Binput) >> Shamt;    //Sra rd,rt,shamt
                3'b111: Shift_Result = $signed(Binput) >> Ainput;   //Srav rd,rt,rs
                default: Shift_Result = Binput;
            endcase
        else
            Shift_Result = Binput;
    end

    always @* begin
        //set type operation (slt, slti, sltu, sltiu)
        if(((ALU_ctl == 3'b111) && (Exe_code[3] == 1)) || ((I_format == 1) && (ALU_ctl == 3'b110)))
            ALU_Result = ($signed(Ainput) < $signed(Binput)) ? 1:0;
        else if((ALU_ctl == 3'b101) && (I_format == 1))
            ALU_Result[31:0] = {Binput[15:0], 16'b0};
        else if(Sftmd == 1)
            ALU_Result = Shift_Result;
        else
            ALU_Result = ALU_output_mux[31:0];
    end

endmodule

