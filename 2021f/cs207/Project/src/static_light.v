`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2021/12/16 10:54:02
// Design Name: 
// Module Name: static_light
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


module static_light(rst,clk,DIG,Y,x7_in,x6_in,x5_in,x4_in,x3_in,x2_in,x1_in,x0_in);
input rst;
 input clk;
 output [7:0] DIG;
 output  [7:0] Y;
input [5:0] x0_in;
input [5:0] x1_in;
input [5:0] x2_in;
input [5:0] x3_in;
input [5:0] x4_in;
input [5:0] x5_in;
input [5:0] x6_in;
input [5:0] x7_in;
 reg clkout;
 reg [31:0] cnt;
 reg [2:0] scan_cnt;
 reg [6:0] x0;
 reg [6:0] x1;
 reg [6:0] x2;
 reg [6:0] x3;
 reg [6:0] x4;
 reg [6:0] x5;
 reg [6:0] x6;
 reg [6:0] x7;
 

always @(*)
begin            
  if(!rst) begin 
      x0=7'b0000000;
      x1=7'b0000000;
      x2=7'b0000000;
      x3=7'b0000000;
      x4=7'b0000000;
      x5=7'b0000000;
      x6=7'b0000000;
      x7=7'b0000000;
  end 
  else           
begin
 if (x0_in == 6'd0) x0=7'b0111111;//0
 else if (x0_in == 6'd1) x0=7'b0000110;//1
 else if (x0_in == 6'd2) x0=7'b1011011;//2
 else if (x0_in == 6'd3) x0=7'b1001111;//3
 else if (x0_in == 6'd4) x0=7'b1100110;//4
 else if (x0_in == 6'd5) x0=7'b1101101;//5
 else if (x0_in == 6'd6) x0=7'b1111101;//6
 else if (x0_in == 6'd7) x0=7'b0100111;//7
 else if (x0_in == 6'd8) x0=7'b1111111;//8
 else if (x0_in == 6'd9) x0=7'b1100111;//9
 else if (x0_in == 6'd10) x0=7'b1110111;//a
 else if (x0_in == 6'd11) x0=7'b1111100;//b
 else if (x0_in == 6'd12) x0=7'b0111001;//c
 else if (x0_in == 6'd13) x0=7'b1011110;//d
 else if (x0_in == 6'd14) x0=7'b1111001;//e
 else if (x0_in == 6'd15) x0=7'b1110001;//f
 else if (x0_in == 6'd16) x0=7'b0111101;//g
 else if (x0_in == 6'd17) x0=7'b1110110;//h
 else if (x0_in == 6'd18) x0=7'b0001111;//i
 else if (x0_in == 6'd19) x0=7'b0001110;//j
 else if (x0_in == 6'd20) x0=7'b1110101;//k
 else if (x0_in == 6'd21) x0=7'b0111000;//l
 else if (x0_in == 6'd22) x0=7'b0110111;//m
 else if (x0_in == 6'd23) x0=7'b1010100;//n
 else if (x0_in == 6'd24) x0=7'b1011100;//o
 else if (x0_in == 6'd25) x0=7'b1110011;//p
 else if (x0_in == 6'd26) x0=7'b1100111;//q
 else if (x0_in == 6'd27) x0=7'b0110001;//r
 else if (x0_in == 6'd28) x0=7'b1001001;//s
 else if (x0_in == 6'd29) x0=7'b1111000;//t
 else if (x0_in == 6'd30) x0=7'b0111110;//u
 else if (x0_in == 6'd31) x0=7'b0011100;//v
 else if (x0_in == 6'd32) x0=7'b1111110;//w
 else if (x0_in == 6'd33) x0=7'b1100100;//x
 else if (x0_in == 6'd34) x0=7'b1101110;//y
 else if (x0_in == 6'd35) x0=7'b1011010;//z
 else if (x0_in == 6'b111_111) x0=7'b0000000;//空格
 
if (x1_in == 6'd0) x1=7'b0111111;//0  
else if (x1_in == 6'd1) x1=7'b0000110;//1  
else if (x1_in == 6'd2) x1=7'b1011011;//2  
else if (x1_in == 6'd3) x1=7'b1001111;//3  
else if (x1_in == 6'd4) x1=7'b1100110;//4  
else if (x1_in == 6'd5) x1=7'b1101101;//5  
else if (x1_in == 6'd6) x1=7'b1111101;//6  
else if (x1_in == 6'd7) x1=7'b0100111;//7  
else if (x1_in == 6'd8) x1=7'b1111111;//8  
else if (x1_in == 6'd9) x1=7'b1100111;//9 
else if (x1_in == 6'd10) x1=7'b1110111;//a
else if (x1_in == 6'd11) x1=7'b1111100;//b
else if (x1_in == 6'd12) x1=7'b0111001;//c
else if (x1_in == 6'd13) x1=7'b1011110;//d
else if (x1_in == 6'd14) x1=7'b1111001;//e
else if (x1_in == 6'd15) x1=7'b1110001;//f
else if (x1_in == 6'd16) x1=7'b0111101;//g
else if (x1_in == 6'd17) x1=7'b1110110;//h
else if (x1_in == 6'd18) x1=7'b0001111;//i
else if (x1_in == 6'd19) x1=7'b0001110;//j
else if (x1_in == 6'd20) x1=7'b1110101;//k
else if (x1_in == 6'd21) x1=7'b0111000;//l
else if (x1_in == 6'd22) x1=7'b0110111;//m
else if (x1_in == 6'd23) x1=7'b1010100;//n
else if (x1_in == 6'd24) x1=7'b1011100;//o
else if (x1_in == 6'd25) x1=7'b1110011;//p
else if (x1_in == 6'd26) x1=7'b1100111;//q
else if (x1_in == 6'd27) x1=7'b0110001;//r
else if (x1_in == 6'd28) x1=7'b1001001;//s
else if (x1_in == 6'd29) x1=7'b1111000;//t
else if (x1_in == 6'd30) x1=7'b0111110;//u
else if (x1_in == 6'd31) x1=7'b0011100;//v
else if (x1_in == 6'd32) x1=7'b1111110;//w
else if (x1_in == 6'd33) x1=7'b1100100;//x
else if (x1_in == 6'd34) x1=7'b1101110;//y
else if (x1_in == 6'd35) x1=7'b1011010;//z
else if (x1_in ==6'b111_111) x1=7'b0000000;//空格

if (x2_in ==6'd0) x2=7'b0111111;//0  
else if (x2_in ==6'd1) x2=7'b0000110;//1  
else if (x2_in ==6'd2) x2=7'b1011011;//2  
else if (x2_in ==6'd3) x2=7'b1001111;//3  
else if (x2_in ==6'd4) x2=7'b1100110;//4  
else if (x2_in ==6'd5) x2=7'b1101101;//5  
else if (x2_in ==6'd6) x2=7'b1111101;//6  
else if (x2_in ==6'd7) x2=7'b0100111;//7  
else if (x2_in ==6'd8) x2=7'b1111111;//8  
else if (x2_in ==6'd9) x2=7'b1100111;//9 
else if (x2_in == 6'd10) x2=7'b1110111;//a
else if (x2_in == 6'd11) x2=7'b1111100;//b
else if (x2_in == 6'd12) x2=7'b0111001;//c
else if (x2_in == 6'd13) x2=7'b1011110;//d
else if (x2_in == 6'd14) x2=7'b1111001;//e
else if (x2_in == 6'd15) x2=7'b1110001;//f
else if (x2_in == 6'd16) x2=7'b0111101;//g
else if (x2_in == 6'd17) x2=7'b1110110;//h
else if (x2_in == 6'd18) x2=7'b0001111;//i
else if (x2_in == 6'd19) x2=7'b0001110;//j
else if (x2_in == 6'd20) x2=7'b1110101;//k
else if (x2_in == 6'd21) x2=7'b0111000;//l
else if (x2_in == 6'd22) x2=7'b0110111;//m
else if (x2_in == 6'd23) x2=7'b1010100;//n
else if (x2_in == 6'd24) x2=7'b1011100;//o
else if (x2_in == 6'd25) x2=7'b1110011;//p
else if (x2_in == 6'd26) x2=7'b1100111;//q
else if (x2_in == 6'd27) x2=7'b0110001;//r
else if (x2_in == 6'd28) x2=7'b1001001;//s
else if (x2_in == 6'd29) x2=7'b1111000;//t
else if (x2_in == 6'd30) x2=7'b0111110;//u
else if (x2_in == 6'd31) x2=7'b0011100;//v
else if (x2_in == 6'd32) x2=7'b1111110;//w
else if (x2_in == 6'd33) x2=7'b1100100;//x
else if (x2_in == 6'd34) x2=7'b1101110;//y
else if (x2_in == 6'd35) x2=7'b1011010;//z
else if (x2_in == 6'b111_111) x2=7'b0000000;//空格

if (x3_in == 6'd0) x3=7'b0111111;//0  
else if (x3_in == 6'd1) x3=7'b0000110;//1  
else if (x3_in == 6'd2) x3=7'b1011011;//2  
else if (x3_in == 6'd3) x3=7'b1001111;//3  
else if (x3_in == 6'd4) x3=7'b1100110;//4  
else if (x3_in == 6'd5) x3=7'b1101101;//5  
else if (x3_in == 6'd6) x3=7'b1111101;//6  
else if (x3_in == 6'd7) x3=7'b0100111;//7  
else if (x3_in == 6'd8) x3=7'b1111111;//8  
else if (x3_in == 6'd9) x3=7'b1100111;//9 
else if (x3_in == 6'd10) x3=7'b1110111;//a
else if (x3_in == 6'd11) x3=7'b1111100;//b
else if (x3_in == 6'd12) x3=7'b0111001;//c
else if (x3_in == 6'd13) x3=7'b1011110;//d
else if (x3_in == 6'd14) x3=7'b1111001;//e
else if (x3_in == 6'd15) x3=7'b1110001;//f
else if (x3_in == 6'd16) x3=7'b0111101;//g
else if (x3_in == 6'd17) x3=7'b1110110;//h
else if (x3_in == 6'd18) x3=7'b0001111;//i
else if (x3_in == 6'd19) x3=7'b0001110;//j
else if (x3_in == 6'd20) x3=7'b1110101;//k
else if (x3_in == 6'd21) x3=7'b0111000;//l
else if (x3_in == 6'd22) x3=7'b0110111;//m
else if (x3_in == 6'd23) x3=7'b1010100;//n
else if (x3_in == 6'd24) x3=7'b1011100;//o
else if (x3_in == 6'd25) x3=7'b1110011;//p
else if (x3_in == 6'd26) x3=7'b1100111;//q
else if (x3_in == 6'd27) x3=7'b0110001;//r
else if (x3_in == 6'd28) x3=7'b1001001;//s
else if (x3_in == 6'd29) x3=7'b1111000;//t
else if (x3_in == 6'd30) x3=7'b0111110;//u
else if (x3_in == 6'd31) x3=7'b0011100;//v
else if (x3_in == 6'd32) x3=7'b1111110;//w
else if (x3_in == 6'd33) x3=7'b1100100;//x
else if (x3_in == 6'd34) x3=7'b1101110;//y
else if (x3_in == 6'd35) x3=7'b1011010;//z
else if (x3_in == 6'b111_111) x3=7'b0000000;//空格 
             
if (x4_in == 6'd0) x4=7'b0111111;//0  
else if (x4_in == 6'd1) x4=7'b0000110;//1  
else if (x4_in == 6'd2) x4=7'b1011011;//2  
else if (x4_in == 6'd3) x4=7'b1001111;//3  
else if (x4_in == 6'd4) x4=7'b1100110;//4  
else if (x4_in == 6'd5) x4=7'b1101101;//5  
else if (x4_in == 6'd6) x4=7'b1111101;//6  
else if (x4_in == 6'd7) x4=7'b0100111;//7  
else if (x4_in == 6'd8) x4=7'b1111111;//8  
else if (x4_in == 6'd9) x4=7'b1100111;//9  
else if (x4_in == 6'd10) x4=7'b1110111;//a
else if (x4_in == 6'd11) x4=7'b1111100;//b
else if (x4_in == 6'd12) x4=7'b0111001;//c
else if (x4_in == 6'd13) x4=7'b1011110;//d
else if (x4_in == 6'd14) x4=7'b1111001;//e
else if (x4_in == 6'd15) x4=7'b1110001;//f
else if (x4_in == 6'd16) x4=7'b0111101;//g
else if (x4_in == 6'd17) x4=7'b1110110;//h
else if (x4_in == 6'd18) x4=7'b0001111;//i
else if (x4_in == 6'd19) x4=7'b0001110;//j
else if (x4_in == 6'd20) x4=7'b1110101;//k
else if (x4_in == 6'd21) x4=7'b0111000;//l
else if (x4_in == 6'd22) x4=7'b0110111;//m
else if (x4_in == 6'd23) x4=7'b1010100;//n
else if (x4_in == 6'd24) x4=7'b1011100;//o
else if (x4_in == 6'd25) x4=7'b1110011;//p
else if (x4_in == 6'd26) x4=7'b1100111;//q
else if (x4_in == 6'd27) x4=7'b0110001;//r
else if (x4_in == 6'd28) x4=7'b1001001;//s
else if (x4_in == 6'd29) x4=7'b1111000;//t
else if (x4_in == 6'd30) x4=7'b0111110;//u
else if (x4_in == 6'd31) x4=7'b0011100;//v
else if (x4_in == 6'd32) x4=7'b1111110;//w
else if (x4_in == 6'd33) x4=7'b1100100;//x
else if (x4_in == 6'd34) x4=7'b1101110;//y
else if (x4_in == 6'd35) x4=7'b1011010;//z
else if (x4_in == 6'b111_111) x4=7'b0000000;//空格

if (x5_in == 6'd0) x5=7'b0111111;//0  
else if (x5_in == 6'd1) x5=7'b0000110;//1  
else if (x5_in == 6'd2) x5=7'b1011011;//2  
else if (x5_in == 6'd3) x5=7'b1001111;//3  
else if (x5_in == 6'd4) x5=7'b1100110;//4  
else if (x5_in == 6'd5) x5=7'b1101101;//5  
else if (x5_in == 6'd6) x5=7'b1111101;//6  
else if (x5_in == 6'd7) x5=7'b0100111;//7  
else if (x5_in == 6'd8) x5=7'b1111111;//8  
else if (x5_in == 6'd9) x5=7'b1100111;//9 
else if (x5_in == 6'd10) x5=7'b1110111;//a
else if (x5_in == 6'd11) x5=7'b1111100;//b
else if (x5_in == 6'd12) x5=7'b0111001;//c
else if (x5_in == 6'd13) x5=7'b1011110;//d
else if (x5_in == 6'd14) x5=7'b1111001;//e
else if (x5_in == 6'd15) x5=7'b1110001;//f
else if (x5_in == 6'd16) x5=7'b0111101;//g
else if (x5_in == 6'd17) x5=7'b1110110;//h
else if (x5_in == 6'd18) x5=7'b0001111;//i
else if (x5_in == 6'd19) x5=7'b0001110;//j
else if (x5_in == 6'd20) x5=7'b1110101;//k
else if (x5_in == 6'd21) x5=7'b0111000;//l
else if (x5_in == 6'd22) x5=7'b0110111;//m
else if (x5_in == 6'd23) x5=7'b1010100;//n
else if (x5_in == 6'd24) x5=7'b1011100;//o
else if (x5_in == 6'd25) x5=7'b1110011;//p
else if (x5_in == 6'd26) x5=7'b1100111;//q
else if (x5_in == 6'd27) x5=7'b0110001;//r
else if (x5_in == 6'd28) x5=7'b1001001;//s
else if (x5_in == 6'd29) x5=7'b1111000;//t
else if (x5_in == 6'd30) x5=7'b0111110;//u
else if (x5_in == 6'd31) x5=7'b0011100;//v
else if (x5_in == 6'd32) x5=7'b1111110;//w
else if (x5_in == 6'd33) x5=7'b1100100;//x
else if (x5_in == 6'd34) x5=7'b1101110;//y
else if (x5_in == 6'd35) x5=7'b1011010;//z
else if (x5_in == 6'b111_111) x5=7'b0000000;//空格 

if (x6_in == 6'd0) x6=7'b0111111;//0  
else if (x6_in == 6'd1) x6=7'b0000110;//1  
else if (x6_in == 6'd2) x6=7'b1011011;//2  
else if (x6_in == 6'd3) x6=7'b1001111;//3  
else if (x6_in == 6'd4) x6=7'b1100110;//4  
else if (x6_in == 6'd5) x6=7'b1101101;//5  
else if (x6_in == 6'd6) x6=7'b1111101;//6  
else if (x6_in == 6'd7) x6=7'b0100111;//7  
else if (x6_in == 6'd8) x6=7'b1111111;//8  
else if (x6_in == 6'd9) x6=7'b1100111;//9 
else if (x6_in == 6'd10) x6=7'b1110111;//a
else if (x6_in == 6'd11) x6=7'b1111100;//b
else if (x6_in == 6'd12) x6=7'b0111001;//c
else if (x6_in == 6'd13) x6=7'b1011110;//d
else if (x6_in == 6'd14) x6=7'b1111001;//e
else if (x6_in == 6'd15) x6=7'b1110001;//f
else if (x6_in == 6'd16) x6=7'b0111101;//g
else if (x6_in == 6'd17) x6=7'b1110110;//h
else if (x6_in == 6'd18) x6=7'b0001111;//i
else if (x6_in == 6'd19) x6=7'b0001110;//j
else if (x6_in == 6'd20) x6=7'b1110101;//k
else if (x6_in == 6'd21) x6=7'b0111000;//l
else if (x6_in == 6'd22) x6=7'b0110111;//m
else if (x6_in == 6'd23) x6=7'b1010100;//n
else if (x6_in == 6'd24) x6=7'b1011100;//o
else if (x6_in == 6'd25) x6=7'b1110011;//p
else if (x6_in == 6'd26) x6=7'b1100111;//q
else if (x6_in == 6'd27) x6=7'b0110001;//r
else if (x6_in == 6'd28) x6=7'b1001001;//s
else if (x6_in == 6'd29) x6=7'b1111000;//t
else if (x6_in == 6'd30) x6=7'b0111110;//u
else if (x6_in == 6'd31) x6=7'b0011100;//v
else if (x6_in == 6'd32) x6=7'b1111110;//w
else if (x6_in == 6'd33) x6=7'b1100100;//x
else if (x6_in == 6'd34) x6=7'b1101110;//y
else if (x6_in == 6'd15) x6=7'b1011010;//z
else if (x6_in == 6'b111_111) x6=7'b0000000;//空格 

if (x7_in == 6'd0) x7=7'b0111111;//0  
if (x7_in == 6'd1) x7=7'b0000110;//1  
else if (x7_in == 6'd2) x7=7'b1011011;//2  
else if (x7_in == 6'd3) x7=7'b1001111;//3  
else if (x7_in == 6'd4) x7=7'b1100110;//4  
else if (x7_in == 6'd5) x7=7'b1101101;//5  
else if (x7_in == 6'd6) x7=7'b1111101;//6  
else if (x7_in == 6'd7) x7=7'b0100111;//7  
else if (x7_in == 6'd8) x7=7'b1111111;//8  
else if (x7_in == 6'd9) x7=7'b1100111;//9  
else if (x7_in == 6'd10) x7=7'b1110111;//a
else if (x7_in == 6'd11) x7=7'b1111100;//b
else if (x7_in == 6'd12) x7=7'b0111001;//c
else if (x7_in == 6'd13) x7=7'b1011110;//d
else if (x7_in == 6'd14) x7=7'b1111001;//e
else if (x7_in == 6'd15) x7=7'b1110001;//f
else if (x7_in == 6'd16) x7=7'b0111101;//g
else if (x7_in == 6'd17) x7=7'b1110110;//h
else if (x7_in == 6'd18) x7=7'b0001111;//i
else if (x7_in == 6'd19) x7=7'b0001110;//j
else if (x7_in == 6'd20) x7=7'b1110101;//k
else if (x7_in == 6'd21) x7=7'b0111000;//l
else if (x7_in == 6'd22) x7=7'b0110111;//m
else if (x7_in == 6'd23) x7=7'b1010100;//n
else if (x7_in == 6'd24) x7=7'b1011100;//o
else if (x7_in == 6'd25) x7=7'b1110011;//p
else if (x7_in == 6'd26) x7=7'b1100111;//q
else if (x7_in == 6'd27) x7=7'b0110001;//r
else if (x7_in == 6'd28) x7=7'b1001001;//s
else if (x7_in == 6'd29) x7=7'b1111000;//t
else if (x7_in == 6'd30) x7=7'b0111110;//u
else if (x7_in == 6'd31) x7=7'b0011100;//v
else if (x7_in == 6'd32) x7=7'b1111110;//w
else if (x7_in == 6'd33) x7=7'b1100100;//x
else if (x7_in == 6'd34) x7=7'b1101110;//y
else if (x7_in == 6'd15) x7=7'b1011010;//z
else if (x7_in ==6'b111_111) x7=7'b0000000;//空格
 end
end
 parameter period = 200000;
 
 reg[6:0] Y_r;
 reg [7:0] DIG_r;
 assign Y= {1'b1,(~Y_r[6:0])};
 assign DIG= ~DIG_r;

 
 always @(posedge clk or negedge rst)
 begin
   if(!rst) begin
     cnt<=0;
     clkout<=0;
   end
   else begin
      if(cnt == (period>>1)-1)
      begin
       clkout<=~clkout;
       cnt<=0;
   end
   else
     cnt<=cnt+1;
  end
 end
 


always @ (posedge clkout or negedge rst)
begin
 if(!rst)
   scan_cnt<=0;
 else begin
   scan_cnt <= scan_cnt + 1;
   if(scan_cnt == 3'd7) scan_cnt <= 0;  
 end
end

always @ (scan_cnt)
begin
  case(scan_cnt)
    3'b000: DIG_r=8'b0000_0001;
    3'b001: DIG_r=8'b0000_0010;
    3'b010: DIG_r=8'b0000_0100;
    3'b011: DIG_r=8'b0000_1000;
    3'b100: DIG_r=8'b0001_0000;
    3'b101: DIG_r=8'b0010_0000;
    3'b110: DIG_r=8'b0100_0000;
    3'b111: DIG_r=8'b1000_0000;
    default: DIG_r = 8'b0000_0000;
    endcase
end


always @ (scan_cnt)
begin
 case(scan_cnt)
  0:Y_r= x0;
  1:Y_r= x1;
  2:Y_r= x2;
  3:Y_r= x3;
  4:Y_r= x4;
  5:Y_r= x5;
  6:Y_r= x6;
  7:Y_r= x7;
  default : Y_r= 7'b0000000;
  endcase
 end
endmodule
