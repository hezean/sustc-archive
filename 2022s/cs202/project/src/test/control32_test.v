module control32_test();

reg[5:0] opcode;
reg[5:0] function_opcode;

wire jr, reg_dst, alu_src, mem_to_reg, reg_write, mem_write, branch, n_branch, jmp, jal, i_format, sftmd;
wire[1:0] alu_op;

control32 ctrl32(
    .Opcode(opcode),
    .Function_opcode(function_opcode),
    .Jr(jr),
    .RegDST(reg_dst),
    .ALUSrc(alu_src),
    .MemtoReg(mem_to_reg),
    .RegWrite(reg_write),
    .MemWrite(mem_write),
    .Branch(branch),
    .nBranch(n_branch),
    .Jmp(jmp),
    .Jal(jal),
    .I_format(i_format),
    .Sftmd(sftmd),
    .ALUOp(alu_op)
);

initial begin

    $dumpfile("control32.vcd");
    $dumpvars(0, control32_test);

    opcode = 0;
    function_opcode = 0;

    // R-formats, Opcode = 0, 17 inst

    #0 function_opcode = 'b00_0000;  // sll
    #5 function_opcode = 'b00_0010;  // srl
    #5 function_opcode = 'b00_0100;  // sllv
    #5 function_opcode = 'b00_0110;  // srlv
    #5 function_opcode = 'b00_0011;  // sra
    #5 function_opcode = 'b00_0111;  // srav

    #5 function_opcode = 'b00_1000;  // jr

    #5 function_opcode = 'b10_0000;  // add
    #5 function_opcode = 'b10_0001;  // addu
    #5 function_opcode = 'b10_0010;  // sub
    #5 function_opcode = 'b10_0011;  // subu

    #5 function_opcode = 'b10_0100;  // and
    #5 function_opcode = 'b10_0101;  // or
    #5 function_opcode = 'b10_0110;  // xor
    #5 function_opcode = 'b10_0111;  // nor

    #5 function_opcode = 'b10_1010;  // slt
    #5 function_opcode = 'b10_1011;  // sltu


    // I-formats, no func-opcode, 12 inst

    #5 opcode = 'b00_0100;  // beq
    #5 opcode = 'b00_0101;  // bne

    #5 opcode = 'b10_0011;  // lw
    #5 opcode = 'b10_1011;  // sw

    #5 opcode = 'b00_1000;  // addi
    #5 opcode = 'b00_1001;  // addiu

    #5 opcode = 'b00_1010;  // slti
    #5 opcode = 'b00_1011;  // sltiu

    #5 opcode = 'b00_1100;  // andi
    #5 opcode = 'b00_1101;  // ori
    #5 opcode = 'b00_1110;  // xori

    #5 opcode = 'b00_1111;  // lui


    // J-formats, no func-opcode, 2 inst

    #5 opcode = 'b00_0010;  // j
    #5 opcode = 'b00_0011;  // jal

    #5 $finish();
end

endmodule