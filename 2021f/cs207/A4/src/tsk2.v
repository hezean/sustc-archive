`timescale 1ns/1ps

module sr74195(
    input cp, mr_n, pe_n,
    input j, k_n,
    input d3, d2, d1, d0,
    output reg q3, q2, q1, q0,
    output q0_n
);
assign q0_n = ~q0;

always @ (posedge cp, negedge mr_n) begin
    if (!mr_n) begin
        {q3, q2, q1, q0} <= 'b0000;
    end else begin
        if (!pe_n) begin
            {q3, q2, q1, q0} <= {d3, d2, d1, d0};
        end else begin
            case ({j, ~k_n})
                'b00: {q3, q2, q1, q0} <= {q2, q1, q0, q0};
                'b11: {q3, q2, q1, q0} <= {q2, q1, q0, ~q0};
                'b10: {q3, q2, q1, q0} <= {q2, q1, q0, 1'b1};
                'b01: {q3, q2, q1, q0} <= {q2, q1, q0, 1'b0};
            endcase
        end
    end
end
endmodule


module johnson(
    input clk, rst_n,
    output reg [3:0] out
);
wire [2:0] qs;
sr74195 sr(clk, rst_n, 0, 0, 0,
           ~out[0], out[3], out[2], out[1],
           qs[3], qs[2], qs[1], qs[0]);

always @ * begin
    if (!rst_n) out <= 3'b000;
    else out <= qs;
end
endmodule