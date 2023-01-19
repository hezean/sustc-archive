`timescale 1ns/1ps

module UnsignedAdditionSim ();
    // for testing 1-bit adder
    reg a, b;
    reg [1:0] sum1;
    UnsignedAdditionDF#(1) adder1bit(.a(a), .b(b), .sum(sum1));

    // for testing 2-bit adder
    reg [1:0] c, d;
    reg [2:0] sum2;
    UnsignedAdditionDF#(2) adder2bit(.a(c), .b(d), .sum(sum2));

    initial begin
        {a,b,c,d} = 0;  // init, RHS is actually 6'b000000
        #160 $finish;
    end

    initial begin
        while({a,b} < 2'b11)
            #10 {a,b} = {a,b} + 1;
    end

    initial begin
        while({c,d} < 4'b1111)
            #10 {c,d} = {c,d} + 1;
    end
endmodule