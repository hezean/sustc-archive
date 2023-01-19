// distributive2bit_sim.v
`timescale 1ns/1ps

module Distributive2bit_sim ();
    reg [1:0] a, b, c;
    wire [1:0] alhs_df, alhs_sd, arhs_df, arhs_sd;
    wire [1:0] blhs_df, blhs_sd, brhs_df, brhs_sd;
    wire [1:0] aissame_df, aissame_sd;
    wire [1:0] bissame_df, bissame_sd;

    Distributive2bit_df d2df(a, b, c, alhs_df, arhs_df, blhs_df, brhs_df, aissame_df, bissame_df);
    Distributive2bit_sd d2sd(a, b, c, alhs_sd, arhs_sd, blhs_sd, brhs_sd, aissame_sd, bissame_sd);

    initial begin
        {a,b,c} = 0;
        while ({a,b,c} < 6'b111111)
            #10 {a,b,c} = {a,b,c} + 1;
        #10 $finish;
    end
endmodule
