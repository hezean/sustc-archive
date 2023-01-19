module decode32_test();

reg[31:0] instruction;
reg[31:0] mem_data;
reg[31:0] alu_result;

reg jal, reg_write, reg_dst, mem_to_reg, opcplus4;
reg clk, rst;

wire[31:0] mem1;
wire[31:0] mem2;
wire[31:0] sign_ext;

decode32 dcd(
    .Instruction(instruction),
    .mem_data(mem_data),
    .ALU_result(alu_result),
    .Jal(jal),
    .RegWrite(reg_write),
    .RegDst(reg_dst),
    .MemtoReg(mem_to_reg),
    .opcplus4(opcplus4),
    .clock(clk),
    .reset(rst),
    .read_data_1(mem1),
    .read_data_2(mem2),
    .Sign_extend(sign_ext)
);

initial begin
    mem_data = 'hFFFF_FFFF;
    alu_result = 'h8888_8888;

    
end

endmodule