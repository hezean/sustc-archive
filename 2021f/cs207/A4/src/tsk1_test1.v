`timescale 1ns/1ps

module jkff_test();
reg clk, rst;
reg j, k;
wire q, qn;

jkff jk(clk, rst, j, k, q, qn);

initial begin
    clk = 0;
    forever #10 clk = ~clk;
end

initial begin
    {rst, j, k} = 'b000;
    #5;  // to demostrate that FF only 
    while ({rst, j, k} < 'b111) begin
        #20 {rst, j, k} = {rst, j, k} + 'b1;
    end
    #10 $finish();
end
endmodule
