`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2021/12/16 11:40:32
// Design Name: 
// Module Name: display_top
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


module display_top(rst,clk,left,start,each,id1,id0,remain1,remain0,x7,x6,x5,x4,x3,x2,x1,x0,DIG,Y); //rst,left,start,each,id1,id0,remain1,remain0,x7,x6,x5,x4,x3,x2,x1,x0
input [1:0] rst;
input clk;
input [3:0] left;
input [3:0] start;
input [3:0] each;
input [3:0] id1;
input [3:0] id0;
input [3:0] remain1;
input [3:0] remain0;
input [5:0] x7;
input [5:0] x6;
input [5:0] x5;
input [5:0] x4;
input [5:0] x3;
input [5:0] x2;
input [5:0] x1;
input [5:0] x0;
output reg [7:0] DIG;
output reg [7:0] Y;  

reg rst_tmp1;
reg rst_tmp2;
reg rst_tmp3; 
wire [7:0] DIG1;
wire [7:0] DIG2;
wire [7:0] DIG3;
wire [7:0] Y1;
wire [7:0] Y2;
wire [7:0] Y3;
 flowinglight u1(rst_tmp1,clk,DIG1,Y1,left,start,each);
 flowinglight_vip u2(rst_tmp2,clk,DIG2,Y2,id1,id0,remain1,remain0);
 static_light u3(rst_tmp3,clk,DIG3,Y3,x7,x6,x5,x4,x3,x2,x1,x0);
 
always@*
begin
if(rst == 2'b01)
   begin 
     rst_tmp1=1;
     rst_tmp2=0;
     rst_tmp3=0;
     DIG=DIG1;
     Y=Y1;
   end
 else if(rst==2'b10)
    begin         
      rst_tmp1=0;   
      rst_tmp2=1;   
      rst_tmp3=0; 
      DIG=DIG2;
      Y=Y2;  
    end
 else if(rst==2'b11) 
    begin            
      rst_tmp1=0;    
      rst_tmp2=0;          
      rst_tmp3=1;  
      DIG=DIG3;
      Y=Y3;        
    end                    
 else     
    begin        
      rst_tmp1=0;
      rst_tmp2=0;
      rst_tmp3=0;  
      DIG=8'b1111_1111;
      Y=8'b1111_1111;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
    end                 
end
endmodule