`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2022/05/20 22:19:17
// Design Name: 
// Module Name: executs32
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////


module executs32(Read_data_1,Read_data_2,Sign_extend,Function_opcode,Exe_opcode,ALUOp,
                 Shamt,ALUSrc,I_format,Zero,Comp,Jr,Sftmd,ALU_Result,Addr_Result,PC_plus_4,
                 HI
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
    output       Comp;              // 1 is true for blez, bgtz instruction 
    output reg[31:0] ALU_Result;        // 计算的数据结果
    output[31:0] Addr_Result;		// 计算的地址结果  
    output reg[31:0] HI;                //Hi 32-bit of mult / div      
    

    wire[31:0] Ainput,Binput;       // two operands for calculation
    wire[5:0] Exe_code;             // use to generate ALU_ctrl. (I_format==0) ? Function_opcode : { 3'b000 , Opcode[2:0] };
    wire[2:0] ALU_ctl;              // the control signals which affact operation in ALU directely
    wire[2:0] Sftm;                 // identify the types of shift instruction, equals to Function_opcode[2:0]
    wire times;
    wire[3:0] ALU_coctl;
    reg[31:0] Shift_Result;         // the result of shift operation
    reg[31:0] ALU_output_mux;
    reg[31:0] ALU_output_comux;
    reg[63:0] mul;
    reg[31:0] hi;
    wire[32:0] Branch_Addr;         // the calculated address of the instruction, Addr_Result is Branch_Addr[31:0]
   

    assign Ainput = Read_data_1;
    assign Binput = (ALUSrc == 0) ? Read_data_2 : Sign_extend[31:0];    //Immediate or not
    
    assign Sftm = Function_opcode[2:0];
    assign Exe_code = (I_format == 0) ? Function_opcode : {3'b000, Exe_opcode[2:0]};
    assign ALU_ctl[0] = (Exe_code[0] | Exe_code[3]) & ALUOp[1];
    assign ALU_ctl[1] = ((!Exe_code[2]) | (!ALUOp[1]));
    assign ALU_ctl[2] = (Exe_code[1] & ALUOp[1]) | ALUOp[0];
    assign times = (Function_opcode[5:4] == 2'b01);
    assign ALU_coctl = Function_opcode[3:0];
    assign Zero = (ALU_output_mux[31:0] == 0) ? 1'b1 : 1'b0;
    assign Branch_Addr = PC_plus_4 + (Sign_extend << 2);
    assign Addr_Result = Branch_Addr[31:0];
    assign Comp = (((Exe_opcode == 6'b000110) && ((Ainput[31] == 1'b1) || Ainput == 0)) || ((Exe_opcode == 6'b000111) && ((Ainput[31] == 1'b0) && (Ainput != 0))));

    always @(times or ALU_coctl or Ainput or Binput) begin
        case (ALU_coctl)
            4'b0000:begin   //mthi
                ALU_output_comux = 32'b0;
                hi = Ainput;
            end
            4'b0001:begin   //mfhi
                ALU_output_comux = 32'b0;
                hi = Ainput;
            end
            4'b0010:begin   //mtlo
                ALU_output_comux = Ainput;
                hi = 32'b0;
            end
            4'b0011:begin   //mflo
                ALU_output_comux = Ainput;
                hi = 32'b0;
            end
            4'b1000: begin  //mult
                mul = $signed(Ainput) * $signed(Binput);
                ALU_output_comux = mul[31:0];
                hi = mul[63:32];
            end
            4'b1001: begin  //multu
                mul = $unsigned(Ainput) * $unsigned(Binput);
                ALU_output_comux = mul[31:0];
                hi = mul[63:32];
            end
            4'b1010: begin  //div
                ALU_output_comux = $signed(Ainput) / $signed(Binput);
                hi = $signed(Ainput) % $signed(Binput);
            end
            4'b1011: begin  //divu
                ALU_output_comux = $unsigned(Ainput) / $unsigned(Binput);
                hi = $unsigned(Ainput) % $unsigned(Binput);
            end
            default: begin
                ALU_output_comux = 0;
                hi = 0;
            end
        endcase
    end
    

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
        if(Sftmd) begin
            case (Sftm[2:0])
                3'b000: Shift_Result = Binput << Shamt;     //Sll rd,rt,shamt 
                3'b010: Shift_Result = Binput >> Shamt;     //Srl rd,rt,shamt
                3'b100: Shift_Result = Binput << Ainput;    //Sllv rd,rt,rs
                3'b110: Shift_Result = Binput >> Ainput;    //Srlv rd,rt,rs
                3'b011: Shift_Result = $signed(Binput) >>> Shamt;    //Sra rd,rt,shamt
                3'b111: Shift_Result = $signed(Binput) >>> Ainput;   //Srav rd,rt,rs
                default: Shift_Result = Binput;
            endcase
        end
        else begin
            Shift_Result = Binput;
        end
    end

//    always @* begin
//        //set type operation (slt, slti, sltu, sltiu)
//        if(((ALU_ctl == 3'b111) && (Exe_code[3] == 1)) || ((I_format == 1) && (ALU_ctl[2:1] == 2'b11))) begin
//            ALU_Result = ($signed(Ainput) < $signed(Binput)) ? 1 : 0;
//        end
//        else if((ALU_ctl == 3'b101) && (I_format == 1)) begin
//            ALU_Result[31:0] = {Binput[15:0], 16'b0};
//        end
//        else if(Sftmd == 1) begin
//            ALU_Result = Shift_Result;
//        end
//        else begin
//            ALU_Result = ALU_output_mux[31:0];
//        end
//    end
  always @* begin
        
        if(times) begin //mult, multu, div, divu, mfhi, mflo, mthi, mtlo
            ALU_Result = ALU_output_comux;
            HI = hi;
        end
        //set type operation (slt, slti, sltu, sltiu) 
        else if(((ALU_ctl == 3'b111) && (Exe_code[3] == 1) && (Exe_code[0] == 0)) || ((I_format == 1) && (ALU_ctl[2:0] == 3'b110))) begin
            ALU_Result = ($signed(Ainput) < $signed(Binput)) ? 1 : 0;
            HI = 0;
        end
        else if(((I_format == 1) && (ALU_ctl[2:0] == 3'b111)) || ((ALU_ctl == 3'b111) && (Exe_code[3] == 1) && (Exe_code[0] == 1)))begin
            ALU_Result = (Ainput < Binput) ? 1 : 0;
            HI = 0;
        end
        else if((ALU_ctl == 3'b101) && (I_format == 1)) begin
            ALU_Result[31:0] = {Binput[15:0], 16'b0};
            HI = 0;
        end
        else if(Sftmd == 1) begin
            ALU_Result = Shift_Result;
            HI = 0;
        end
        else begin
            ALU_Result = ALU_output_mux[31:0];
            HI = 0;
        end
    end

endmodule
