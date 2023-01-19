module Main (
    clk, enable, button,music_en, DIG, Y, row, col, music_frac_ext
);
input clk, enable, music_en;
input [4:0] button;
input [3:0] row;
output [3:0] col;
output [7:0] DIG;
output [7:0] Y;
output music_frac_ext;

wire clk2;
wire clk3;
wire [7:0] DIG1;
wire [7:0] Y1;
wire [4:0] buttonVib;
wire [1:0] displayMode;
wire [3:0] stallLeft;
wire [3:0] start;
wire [3:0] per;
wire [3:0] id0;
wire [3:0] id1;
wire [3:0] remain0;
wire [3:0] remain1;
wire [5:0] x0;
wire [5:0] x1;
wire [5:0] x2;
wire [5:0] x3;
wire [5:0] x4;
wire [5:0] x5;
wire [5:0] x6;
wire [5:0] x7;
wire [0:0]finish;
wire [29:0] key;
wire[2:0] music_sel;

freq_div_even #(100000000,31) freq1(clk, enable, clk2);//1s
freq_div_even #(500000, 31) freq2(clk, enable, clk3);//0.1s

ButtonVibration BV(clk, enable, button, buttonVib);//��ť����

musicplayer music(clk, music_sel,music_en, music_frac_ext);

multi_key mult(buttonVib[2], clk, 2, row, col, DIG1, Y1, finish, key);

FSM fsm(buttonVib, clk2, clk3, enable, key, finish, displayMode, stallLeft,start, per, id0, id1, remain0, remain1, x0, x1, x2, x3, x4, x5, x6, x7, music_sel);

display_top display(displayMode, clk, stallLeft, start, per, id0, id1, remain0, remain1, x7, x6, x5, x4, x3, x2, x1, x0, DIG, Y);

endmodule