// distributive1bit_df.v
`timescale 1ns/1ps

module Distributive1bit_df (
    input a, b, c,
    output alhs, arhs, aissame,
    output blhs, brhs, bissame
);
    assign alhs = a & (b | c);
    assign arhs = a & b | a & c;
    assign aissame = ~(alhs ^ arhs);

    assign blhs = a | b & c;
    assign brhs = (a | b) & (a | c);
    assign bissame = ~(blhs ^ brhs);
endmodule
