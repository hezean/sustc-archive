`timescale 1ns/1ps

module task1_test();
reg clk, rst;
reg x;
wire a, b;

task1 tsk1(clk, rst, x, a, b);

initial begin
    clk = 0;
    forever #10 clk = ~clk;
end

initial begin
   {clk, rst, x} = 'b000;
   #550 x = 1;
   #500 $finish(); 
end

initial begin
    #50 rst = ~rst;
end
endmodule
