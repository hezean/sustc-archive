`timescale 1ns/1ps

module task2 (
    input a, b, c,
    output sop, pos
);
    assign sop = ~a & ~b & c | ~a & b & ~c | a & b & c | a & ~b & ~c;
    assign pos = (a | b | c) & (a | ~b | ~c) & (~a | ~b | c) & (~a | b | ~c);
endmodule
