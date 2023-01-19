`timescale 1ns/1ps

module mux_74151_test();
    reg mux_en;
    reg s2, s1, s0;
    reg d0, d1, d2, d3, d4, d5, d6, d7;
    wire Y, W;

    mux_74151 mux(mux_en,
                  s2, s1, s0,
                  d0, d1, d2, d3, d4, d5, d6, d7,
                  Y, W);

    initial begin
        mux_en = 'b1;
        {s2, s1, s0} = 'b000;
        {d0, d1, d2, d3, d4, d5, d6, d7} = 'b1111_1111;

        #10 mux_en = 'b0;
        while ({s2, s1, s0} < 'b111) begin
            #10 {s2, s1, s0} = {s2, s1, s0} + 1;
            {d0, d1, d2, d3, d4, d5, d6, d7} = 'b0000_0001 << {s2, s1, s0};
        end
        #10 $finish();
    end
endmodule
