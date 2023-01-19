`timescale 1ns / 1ps


module decode32(read_data_1,
                read_data_2,
                Instruction,
                mem_data,
                ALU_result,
                ALU_Hi,
                Jal,
                RegWrite,
                MemtoReg,
                RegDst,
                Sign_extend,
                clock,
                reset,
                opcplus4);
    
    input[31:0]  Instruction;               // ȡָ��Ԫ����ָ��
    input[31:0]  ALU_result;   				// ��ִ�е�Ԫ��������Ľ��

    input[31:0]  ALU_Hi;

    input        RegWrite;                  // ���Կ��Ƶ�Ԫ
    input        RegDst;
    
    input        MemtoReg;                  // ���Կ��Ƶ�Ԫ
    input[31:0]  mem_data;   				// ��DATA RAM or I/O portȡ��������
    
    input        Jal;                       // ���Կ��Ƶ�Ԫ��˵����JALָ��
    input[31:0]  opcplus4;                  // ����ȡָ��Ԫ��JAL����
    
    input		 clock, reset;              // ʱ�Ӻ͸�λ
    
    output[31:0] read_data_1;               // ����ĵ�һ������?
    output[31:0] read_data_2;               // ����ĵڶ�������?
    output[31:0] Sign_extend;               // ��չ���?32λ������
    
    
    reg[31:0] reg_file [0:31];              // �Ĵ����飡����

    reg[31:0] hi, lo;

    wire[4:0] rs, rt, rd;

    reg[4:0] write_reg;                     // д��ļĴ���?
    reg[31:0] write_val;                    // д��Ĵ���������?

    integer i;

    wire ext_series_1M;
    wire ext_series_1C;
    wire ext_series_1;
    wire ext_series_2;

    ///////////////////////////////////////////////////////////////////////////////

    assign rs = Instruction[25:21];
    assign rt = Instruction[20:16];
    assign rd = Instruction[15:11];


    assign ext_series_1M = Instruction[31:26] == 0 
                            && Instruction[5:2] == 'b0100;  // mfhi, mflo, mthi, mtlo
    assign ext_series_1C = Instruction[31:26] == 0 
                            && Instruction[5:2] == 'b0110;  // mult, multu, div, divu
    assign ext_series_1 = ext_series_1M || ext_series_1C;

    assign ext_series_2 = Instruction[31:27] == 5'b00011;  // blez, bgtz


    assign Sign_extend[15:0] = Instruction[15:0];
    assign Sign_extend[31:16] = Instruction[31:27] == 5'b00011  // blez, bgtz
                                ? {16{Instruction[15]}}
                                : (Instruction[31:28] == 4'b0011
                                    || Instruction[31:26] == 6'b001010
                                    ? 16'h0000
                                    : {16{Instruction[15]}});


    assign read_data_1 = reg_file[rs];
    assign read_data_2 = ext_series_2 ? 0 : reg_file[rt];

    always @ * begin
        if (RegDst) write_reg <= rd;
        else write_reg <= rt;
        if (Jal && Instruction[31:26] == 'b000_011) write_reg <= 'b11111;
    end

    always @ * begin
        if (Jal) write_val <= opcplus4;
        else if (MemtoReg) write_val <= mem_data;
        else write_val <= ALU_result;
    end

    // register file related
    always @ (posedge clock, posedge reset) begin
        if (reset) begin
            for (i = 0; i < 32; i = i + 1) begin
                reg_file[i] <= 32'b0;
            end
            lo <= 32'b0;
            hi <= 32'b0;
        end
        else if (!ext_series_1 && !ext_series_2) begin
            if (Jal && Instruction[31:26] == 'b000_011) reg_file[31] <= opcplus4;
            else if (RegWrite) begin
                if (write_reg) reg_file[write_reg] <= write_val;  // protect $0 from changing
            end
        end
        else if (ext_series_1M) begin
               if (Instruction[5:0] == 'b010001) begin  // mthi rs
                   hi <= reg_file[rs];
               end
               else if (Instruction[5:0] == 'b010011) begin  // mtlo rs
                   lo <= reg_file[rs];
               end
               else if (Instruction[5:0] == 'b010000) begin  // mfhi rd
                   reg_file[rd] <= hi;
               end
               else if (Instruction[5:0] == 'b010010) begin  // mflo rd
                   reg_file[rd] <= lo;
               end
           end
        else if (ext_series_1C) begin  // the returned alu result are splitted into lo and hi, for mult and div
           lo <= ALU_result;
           hi <= ALU_Hi;
        end
    end

endmodule

