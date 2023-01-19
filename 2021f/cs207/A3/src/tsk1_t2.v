`timescale 1ns/1ps

module decode_4_16_test();
    reg A, B, C, D;
    reg dec_en;
    wire [15:0] Y;

    decode_4_16 dec(A, B, C, D, dec_en, Y);

    initial begin
        dec_en = 'b0;
        repeat(2) begin
            {D, C, B, A} = 'b0000;

            while ({D, C, B, A} < 'b1111) begin
                #10 {D, C, B, A} = {D, C, B, A} + 1;
            end

            #10 dec_en = dec_en + 1;
        end
        #10 $finish();
    end
endmodule
