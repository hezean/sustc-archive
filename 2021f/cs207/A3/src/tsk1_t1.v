`timescale 1ns/1ps

module decode_74138_test();
    reg A, B, C;
    reg G1, G2a, G2b;
    wire [7:0] Y;

    decode_74138 dec(A, B, C, G1, G2a, G2b, Y);

    initial begin
        {G1, G2a, G2b} = 'b000;

        while ({G1, G2a, G2b} < 'b111) begin
            {C, B, A} = 'b000;

            while ({C, B, A} < 'b111) begin
                #10 {C, B, A} = {C, B, A} + 1;
            end
            #10 {G1, G2a, G2b} = {G1, G2a, G2b} + 1;
        end
        #10 $finish();
    end
endmodule
