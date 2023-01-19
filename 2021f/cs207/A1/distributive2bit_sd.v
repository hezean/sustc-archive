// distributive2bit_sd.v
`timescale 1ns/1ps

module Distributive2bit_sd (
    input [1:0] a, b, c,
    output [1:0] alhs, arhs,
    output [1:0] blhs, brhs,
    output [1:0] aissame, bissame
);
// distributive <a>
    wire [1:0] aobc, anab, anac;
    // [0]
    or(aobc[0], b[0], c[0]);
    and(alhs[0], a[0], aobc[0]);

    and(anab[0], a[0], b[0]);
    and(anac[0], a[0], c[0]);
    or(arhs[0], anab[0], anac[0]);

    // [1]
    or(aobc[1], b[1], c[1]);
    and(alhs[1], a[1], aobc[1]);

    and(anab[1], a[1], b[1]);
    and(anac[1], a[1], c[1]);
    or(arhs[1], anab[1], anac[1]);

    // check if lhs==rhs
    xor(axlr0, alhs[0], arhs[0]);
    not(aissame[0], axlr0);
    xor(axlr1, alhs[1], arhs[1]);
    not(aissame[1], axlr1);

// distributive <b>
    wire [1:0] bnbc, boab, boac;
    and(bnbc[0], b[0], c[0]);
    or(blhs[0], a[0], bnbc[0]);

    or(boab[0], a[0], b[0]);
    or(boac[0], a[0], c[0]);
    and(brhs[0], boab[0], boac[0]);

    and(bnbc[1], b[1], c[1]);
    or(blhs[1], a[1], bnbc[1]);
    
    or(boab[1], a[1], b[1]);
    or(boac[1], a[1], c[1]);
    and(brhs[1], boab[1], boac[1]);

    xor(bxlr0, blhs[0], brhs[0]);
    not(bissame[0], bxlr0);
    xor(bxlr1, blhs[1], brhs[1]);
    not(bissame[1], bxlr1);
endmodule
