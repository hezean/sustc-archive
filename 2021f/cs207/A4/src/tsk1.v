`timescale 1ns/1ps

module jkff(
           input clk, rst,
           input j, k,
           output reg q,
           output qn
);
assign qn = ~q;

always @ (posedge clk, negedge rst) begin
    if (!rst) begin
        q <= 0;
    end else begin
        case ({j, k})
            'b00: q <= q;    // keep
            'b11: q <= ~q;   // reverse
            'b10: q <= 1;    // set
            'b01: q <= 0;    // reset
        endcase
    end
end
endmodule


module task1(
    input clk, rst,
    input x,
    output a, b
);
jkff jk1(clk, rst, ~x, b, a);  // leave qn unwired
jkff jk2(clk, rst, x, ~a, b);  // leave qn unwired
endmodule
