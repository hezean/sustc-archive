// distributive1bit_sim.v
`timescale 1ns/1ps

module Distributive1bit_sim ();
    reg a, b, c;
    wire a_lhs_df, a_lhs_sd, a_rhs_df, a_rhs_sd;
    wire a_issame_df, a_issame_sd;

    wire b_lhs_df, b_lhs_sd, b_rhs_df, b_rhs_sd;
    wire b_issame_df, b_issame_sd;

    Distributive1bit_df d1df(a, b, c, a_lhs_df, a_rhs_df, a_issame_df, b_lhs_df, b_rhs_df, b_issame_df);
    Distributive1bit_sd d1sd(a, b, c, a_lhs_sd, a_rhs_sd, a_issame_sd, b_lhs_sd, b_rhs_sd, b_issame_sd);

    initial begin
        {a,b,c} = 0;
        while ({a,b,c} < 3'b111)
            #10 {a,b,c} = {a,b,c} + 1;
        #10 $finish;
    end
endmodule
