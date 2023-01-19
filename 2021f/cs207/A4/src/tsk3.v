`timescale 1ns/1ps

module freq_div_even #(parameter N = 100, WIDTH = 7)(
           input clk,
           input rst,
           output reg clk_out
       );
reg [WIDTH:0] counter;
always @(posedge clk, posedge rst) begin
    if (!rst) begin
        counter <= 0;
    end
    else if (counter == N-1) begin
        counter <= 0;
    end
    else begin
        counter <= counter + 1;
    end
end

always @(posedge clk or posedge rst) begin
    if (!rst) begin
        clk_out <= 0;
    end
    else if (counter == N-1) begin
        clk_out <= !clk_out;
    end
end
endmodule


module seg_tube (
    input clk, mode  // mode ? CS207 : 2021F
    input rst,
    output reg [7:0] seg_en,
    output reg [7:0] seg_out
);

reg [2:0] now_state;

always @ (posedge clk, posedge rst) begin
    if (rst) now_state = 0;
    else begin
        if (now_state == 4) now_state = 0;
        else now_state = now_state + 1;
    end
    seg_en = ~('b1 << now_state);
end

always @ (now_state) begin
    case now_state
        0: seg_out = mode ? 8'b0111_1000  // 7
                          : 8'b0000_1110;  // F
        1: seg_out = mode ? 8'b0100_0000; // 0
                          : 8'b0111_1001  // 1
        2: seg_out =        8'b0010_0100; // 2
        3: seg_out = mode ? 8'b0001_0010; // S
                          : 8'b0100_0000  // 0
        4: seg_out = mode ? 8'b0100_0110  // C
                          : 8'b0010_0100; // 2
    endcase
end
endmodule

module top (
    input clk, rst,
    input mode,
    output [7:0] seg_en, seg_out
);
wire sub_clk;
freq_div_even div(clk, rst, sub_clk);
seg_tube st(sub_clk, mode, rst, seg_en, seg_out);
endmodule
