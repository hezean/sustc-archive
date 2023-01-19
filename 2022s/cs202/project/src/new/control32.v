`timescale 1ns / 1ps


/**
 * Base on the MINISYS ISA which has differences in the instruction encoding to MIPS32
 * extended several instructions that the compiler supports
 */
module control32(Opcode,
                 Function_opcode,
                 Alu_resultHigh,
                 Branch,
                 nBranch,
                 Jr,
                 Jmp,
                 Jal,
                 ALUSrc,
                 ALUOp,
                 MemWrite,
                 MemRead,
                 IORead,
                 IOWrite,
                 RegWrite,
                 RegDST,
                 MemorIOtoReg,
                 I_format,
                 Sftmd);
    
    input[5:0]   Opcode;            // ����IFetchģ���ָ���6bit, instruction[31..26]
    input[5:0]   Function_opcode;  	// ����IFetchģ���ָ���6bit, ��������r-�����е�ָ��, instructions[5..0]
    input[21:0]  Alu_resultHigh;     // ����ALUģ��Ľ����λ, instruction[25..0]
    
    output       Branch;            // Ϊ1������beqָ��, Ϊ0ʱ��ʾ����beqָ��
    output       nBranch;           // Ϊ1������bneָ��, Ϊ0ʱ��ʾ����bneָ��
    
    output       Jr;         	    // Ϊ1������ǰָ����jr, Ϊ0��ʾ��ǰָ���jr
    output       Jmp;               // Ϊ1������Jָ��, Ϊ0ʱ��ʾ����Jָ��
    output       Jal;               // Ϊ1������Jalָ��, Ϊ0ʱ��ʾ����Jalָ��
    
    output       ALUSrc;            // Ϊ1�����ڶ�����������ALU�е�Binput������������beq, bne���⣩, Ϊ0ʱ��ʾ�ڶ������������ԼĴ���
    
    output[1:0]  ALUOp;             // ��R-���ͻ�I_format = 1ʱλ1����bitλ��Ϊ1,  beq��bneָ����λ0����bitλ��Ϊ1
    
    output       MemWrite;          // Ϊ1������ָ����Ҫд�洢��
    output       MemRead;           // Ϊ1������ָ����Ҫ���洢��

    output       IORead;            // Ϊ1������ָ����Ҫ��IO
    output       IOWrite;           // Ϊ1������ָ����ҪдIO
    
    output       RegWrite;    	    // Ϊ1������ָ����Ҫд�Ĵ���
    output       RegDST;            // Ϊ1����Ŀ�ļĴ�����rd, ����Ŀ�ļĴ�����rt
    output       MemorIOtoReg;          // Ϊ1������Ҫ�Ӵ洢����I/O�����ݵ��Ĵ���
    
    output       I_format;          // Ϊ1������ָ���ǳ�beq, bne, LW, SW֮�������I-����ָ��
    output       Sftmd;             // Ϊ1��������λָ��, Ϊ0����������λָ��
    
    
    wire R_format, sw, lw;
    
    ///////////////////////////////////////////////////////////////////////////////
    
    assign R_format = Opcode == 'b000_000;
    assign I_format = Opcode[5:3] == 'b001;  // ignore all branch / load / store

    assign sw = Opcode == 'b101_011;
    assign lw = Opcode == 'b100_011;
    
    assign Jmp = Opcode == 'b000_010;  // J-format
    assign Jal = Opcode == 'b000_011;  // J-format
    assign Jr  = R_format && (Function_opcode == 'b001_000);
    
    assign Branch  = Opcode == 'b000_100;
    assign nBranch = Opcode == 'b000_101;
    
    assign ALUOp = {R_format || I_format, Branch || nBranch};
    
    // only 'sw' for MINISYS
    assign MemWrite =(sw && (Alu_resultHigh[21:0] != 22'h3FFFFF));
    assign MemRead = (lw && (Alu_resultHigh[21:0] != 22'h3FFFFF));

    assign IOWrite = (sw && (Alu_resultHigh[21:0] == 22'h3FFFFF));
    assign IORead = (lw && (Alu_resultHigh[21:0] == 22'h3FFFFF));
    
    assign ALUSrc = I_format || Opcode[5] == 'b1;  // + load / store
    
    // sll, srl, sra, sllv, srlv, srav
    assign Sftmd = R_format && (Function_opcode[5:3] == 'b000);
    
    // R-formats -> rd, // but jr/div/divu/mult/multu don't
    assign RegDST = R_format;
    
    assign RegWrite = R_format && !Jr || I_format || Jal || Opcode == 'b100_011   // lw
            || (Opcode == 0 && Function_opcode[5:4] == 'b01 && Function_opcode[2] == 'b0);  // ext calc

    
    // lwc1, ldc1 -> Opcode = 0x31 / 0x35
    // lbu, lhu, ll, lw -> 0x23, 0x24, 0x25, 0x30
    assign MemorIOtoReg = IORead || MemRead;
endmodule
