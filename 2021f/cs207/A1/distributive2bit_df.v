// distributive2bit_df.v
`timescale 1ns/1ps

module Distributive2bit_df (
    input [1:0] a, b, c,
    output [1:0] alhs, arhs,
    output [1:0] blhs, brhs,
    output [1:0] aissame, bissame
);
    assign alhs = a & (b | c);
    assign arhs = a & b | a & c;
    assign aissame = ~(alhs ^ arhs);

    assign blhs = a | b & c;
    assign brhs = (a | b) & (a | c);
    assign bissame = ~(blhs ^ brhs);
endmodule
