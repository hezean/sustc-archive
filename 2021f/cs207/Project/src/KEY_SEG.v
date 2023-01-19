`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2016/11/02 11:01:06
// Design Name: 
// Module Name: key_top
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////


module key_top(clk,rst,row,col,keyboard_val,key_clk,key_pressed_flag  );
  input           clk;
  input           rst;
  input      [3:0] row;                 // 矩阵键盘 行
  output reg [3:0] col;                // 矩阵键盘 列
  output reg [5:0] keyboard_val;
  output key_clk;
  output reg key_pressed_flag;
  

 
//++++++++++++++++++++++++++++++++++++++
// 分频部分 开始
//++++++++++++++++++++++++++++++++++++++
reg [19:0] cnt;                         // 计数子
//wire key_clk;
 
always @ (posedge clk or posedge rst)
  if (rst )
    cnt <= 0;
  else 
    cnt <= cnt + 1'b1;
    
assign key_clk = cnt[19];                // (2^20/50M = 21)ms 
//--------------------------------------
// 分频部分 结束
//--------------------------------------
 
//++++++++++++++++++++++++++++++++++++++
// 状态机部分 开始
//++++++++++++++++++++++++++++++++++++++
// 状态数较少，独热码编码
parameter NO_KEY_PRESSED = 6'b000_001;  // 没有按键按下  
parameter SCAN_COL0      = 6'b000_010;  // 扫描第0列 
parameter SCAN_COL1      = 6'b000_100;  // 扫描第1列 
parameter SCAN_COL2      = 6'b001_000;  // 扫描第2列 
parameter SCAN_COL3      = 6'b010_000;  // 扫描第3列 
parameter KEY_PRESSED    = 6'b100_000;  // 有按键按下

reg [5:0] current_state, next_state;    // 现态、次态
 
always @ (posedge key_clk or posedge rst)
  if (rst)
    current_state <= NO_KEY_PRESSED;
  else
    current_state <= next_state;
 
// 根据条件转移状态
always @ (*)
  case (current_state)
    NO_KEY_PRESSED :                    // 没有按键按下
        if (row != 4'hF)
          next_state = SCAN_COL0;
        else
          next_state = NO_KEY_PRESSED;
    SCAN_COL0 :                         // 扫描第0列 
        if (row != 4'hF)
          next_state = KEY_PRESSED;
        else
          next_state = SCAN_COL1;
    SCAN_COL1 :                         // 扫描第1列 
        if (row != 4'hF)
          next_state = KEY_PRESSED;
        else
          next_state = SCAN_COL2;    
    SCAN_COL2 :                         // 扫描第2列
        if (row != 4'hF)
          next_state = KEY_PRESSED;
        else
          next_state = SCAN_COL3;
    SCAN_COL3 :                         // 扫描第3列
        if (row != 4'hF)
          next_state = KEY_PRESSED;
        else
          next_state = NO_KEY_PRESSED;
    KEY_PRESSED :                       // 有按键按下
        if (row != 4'hF)
          next_state = KEY_PRESSED;
        else
          next_state = NO_KEY_PRESSED;                      
  endcase
 
//reg       key_pressed_flag;             // 键盘按下标志
reg [3:0] col_val, row_val;             // 列值、行值

 
// 根据次态，给相应寄存器赋值
always @ (posedge key_clk or posedge rst)
  if (rst)
  begin
    col              <= 4'h0;
    key_pressed_flag <=    0;
  end
  else
    case (next_state)
      NO_KEY_PRESSED :                  // 没有按键按下
      begin
        col              <= 4'h0;
        key_pressed_flag <=    0;       // 清键盘按下标志
      end
      SCAN_COL0 :                       // 扫描第0列
        col <= 4'b1110;
      SCAN_COL1 :                       // 扫描第1列
        col <= 4'b1101;
      SCAN_COL2 :                       // 扫描第2列
        col <= 4'b1011;
      SCAN_COL3 :                       // 扫描第3列
        col <= 4'b0111;
      KEY_PRESSED :                     // 有按键按下
      begin
        col_val          <= col;        // 锁存列值
        row_val          <= row;        // 锁存行值
        key_pressed_flag <= 1;          // 置键盘按下标志  
      end
    endcase
//--------------------------------------
// 状态机部分 结束
//--------------------------------------
 
 
//++++++++++++++++++++++++++++++++++++++
// 扫描行列值部分 开始
//++++++++++++++++++++++++++++++++++++++

always @ (posedge key_clk or posedge rst)
  if (rst)
    keyboard_val <= 6'b111_111;
  else
    if (key_pressed_flag)
      case ({col_val, row_val})
        8'b1110_1110 : keyboard_val <=6'h1;
        8'b1110_1101 : keyboard_val <=6'h4;
        8'b1110_1011 : keyboard_val <=6'h7;
        8'b1110_0111 : keyboard_val <=6'hE;
                                      
        8'b1101_1110 : keyboard_val <=6'h2;
        8'b1101_1101 : keyboard_val <=6'h5;
        8'b1101_1011 : keyboard_val <=6'h8;
        8'b1101_0111 : keyboard_val <=6'h0;
                                      
        8'b1011_1110 : keyboard_val <=6'h3;
        8'b1011_1101 : keyboard_val <=6'h6;
        8'b1011_1011 : keyboard_val <=6'h9;
        8'b1011_0111 : keyboard_val <=6'hF;
                                      
        8'b0111_1110 : keyboard_val <=6'hA; 
        8'b0111_1101 : keyboard_val <=6'hB;
        8'b0111_1011 : keyboard_val <=6'hC;
        8'b0111_0111 : keyboard_val <=6'hD;  
        default:   keyboard_val <= 6'b111111;     
      endcase
     else  keyboard_val <= 6'b111111;
//--------------------------------------
//  扫描行列值部分 结束
//--------------------------------------

//--------------------------------------
//  数码管译码显示
//--------------------------------------
//assign seg_an = ~8'b00000001;
//always @ (keyboard_val)
//     begin 
//     case (keyboard_val)
//         4'h0: seg_out = 8'b01000000; // 0
//         4'h1: seg_out = 8'b01111001; // 1
//         4'h2: seg_out = 8'b00100100; // 2
//         4'h3: seg_out = 8'b00110000; // 3
//         4'h4: seg_out = 8'b00011001; // 4
//         4'h5: seg_out = 8'b00010010; // 5
//         4'h6: seg_out = 8'b00000010; // 6
//         4'h7: seg_out = 8'b01111000; // 7
//         4'h8: seg_out = 8'b00000000; // 8
//         4'h9: seg_out = 8'b00010000; // 9
//         4'ha: seg_out = 8'b00001000; // A
//         4'hb: seg_out = 8'b00000011; // b
//         4'hc: seg_out = 8'b01000110; // c
//         4'hd: seg_out = 8'b00100001; // d
//         4'he: seg_out = 8'b00000110; // E
//         4'hf: seg_out = 8'b00001110; // F
//         default: seg_out = 8'b0000000;
//     endcase
//     end    
//     vio_0 your_instance_name (
//  .clk(clk),                // input wire clk
//  .probe_out0(seg_out)  // output wire [7 : 0] probe_out0
//);
endmodule