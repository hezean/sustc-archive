`timescale 1ns/1ps

module johnson_test();
reg clk, rst_n;
wire [3:0] out;

johnson js(clk, rst_n, out);

initial begin
    clk = 0;
    forever #5 clk = ~clk;
end

initial begin
    rst_n = 0;
    #10 rst_n = 1;
    #200 $finish();
end
endmodule
