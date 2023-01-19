`timescale 1ns/1ps

module mux_func_test();
    reg A, B, C, D,
    wire Y_df, Y_1mux, Y_2mux;

    func_df df(A, B, C, D, Y_df);
    func_1mux one_mux(A, B, C, D, Y_1mux);
    func_2mux two_mux(A, B, C, D, Y_2mux);

    initial begin
        {A, B, C, D} = 'b0000;
        while ({A, B, C, D} < 'b1111) begin
            #10 {A, B, C, D} = {A, B, C, D} + 1;
        end
        #10 $finish();
    end
endmodule
