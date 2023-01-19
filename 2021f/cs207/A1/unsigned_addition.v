`timescale 1ns/1ps

module UnsignedAdditionDF
#(parameter WIDTH = 1) (
    input [WIDTH-1:0] a, b,
    output [WIDTH:0] sum
);
    assign sum = a + b;
endmodule

module UnsignedAdditionSD
#(parameter WIDTH = 1) (
    input [WIDTH-1:0] a, b,
    output [WIDTH:0] sum
);
    genvar i;
    wire [WIDTH:0] carry;
    assign carry[0] = 0;
    assign sum[WIDTH] = carry[WIDTH];
    
    generate
        for (i = 0; i < WIDTH; i++) begin
            fullAdder bitAddition(a[i], b[i], carry[i], sum[i], carry[i+1]);
        end
    endgenerate
endmodule

module fullAdder (
    input a, b cin,
    output sum, cout
);
    wire t1, t2, t3;

    xor(sum, a, b, cin);
    and(t1, a, b);
    and(t2, a, cin);
    and(t3, b, cin);
    or(cout, t1, t2, t3);
endmodule
