`timescale 1ns/1ps

module sr74195_test();
reg cp, mr_n, pe_n;
reg j, k_n;
reg d3, d2, d1, d0;
wire q3, q2, q1, q0, q0_n;

sr74195 sr(cp, mr_n, pe_n, j, k_n,
           d3, d2, d1, d0,
           q3, q2, q1, q0, q0_n);

initial begin
    cp = 0;
    forever #5 cp = ~cp;
end

initial begin
    {j, k_n, mr_n, pe_n} = 4'b0000;  // reseting, enable parallel input
    {d3, d2, d1, d0} = 4'b0101;
    #5 mr_n = 1'b1;  // not reseting
    #10 {d3, d2, d1, d0} = 4'b1010;  // demostrating parallel input
    #10 mr_n = 1'b0;  // master reset
    #10 {mr_n, pe_n} = 2'b11;  // not reseting, disable parallel input

    while ({j, k_n} < 2'b11) begin
        #10 {j, k_n} = {j, k_n} + 1;  // each time, shifting in 2 bits
    end
    #10 $finish();
end

endmodule
