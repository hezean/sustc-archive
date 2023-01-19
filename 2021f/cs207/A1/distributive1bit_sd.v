// distributive1bit_sd.v
`timescale 1ns/1ps

module Distributive1bit_sd (
    input a, b, c,
    output alhs, arhs, aissame,
    output blhs, brhs, bissame
);
    // we omit the declations of wires here
    or(aobc, b, c);
    and(alhs, a, aobc);

    and(anab, a, b);
    and(anac, a, c);
    or(arhs, anab, anac);

    xor(axlr, alhs, arhs);
    not(aissame, axlr);

    and(bnbc, b, c);
    or(blhs, a, bnbc);

    or(boab, a, b);
    or(boac, a, c);
    and(brhs, boab, boac);

    xor(bxlr, blhs, brhs);
    not(bissame, bxlr);
endmodule
