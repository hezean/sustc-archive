`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2021/12/16 10:22:09
// Design Name: 
// Module Name: flowinglight_vip
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


module flowinglight_vip(rst,clk,DIG,Y,id1,id0,remain1,remain0);
input rst;
 input clk;
 output [7:0] DIG;
 output  [7:0] Y;
 input [3:0] id0;
 input [3:0] id1;
 input [3:0] remain0;
 input [3:0] remain1;
 reg clkout;
 reg [31:0] cnt;
 reg clkout_flow;
 reg [31:0] cnt_flow;
 reg [2:0] scan_cnt;
 reg [6:0] id0_flow;
 reg  [6:0]id1_flow;
 reg  [6:0]  remain0_flow;
  reg [6:0] remain1_flow;
 reg [6:0] x0;
 reg [6:0] x1;
 reg [6:0] x2;
 reg [6:0] x3;
 reg [6:0] x4;
 reg [6:0] x5;
 reg [6:0] x6;
 reg [6:0] x7;
 reg [3:0] judge;
 

always @(*)
begin            
  if(!rst) begin 
      id0_flow=7'b0000000;
      id1_flow=7'b0000000;
      remain0_flow=7'b0000000;
      remain1_flow=7'b0000000;
  end 
  else           
begin
 if (id0 == 4'd0) id0_flow=7'b0111111;//0
 if (id0 == 4'd1) id0_flow=7'b0000110;//1
 if (id0 == 4'd2) id0_flow=7'b1011011;//2
 if (id0 == 4'd3) id0_flow=7'b1001111;//3
 if (id0 == 4'd4) id0_flow=7'b1100110;//4
 if (id0 == 4'd5) id0_flow=7'b1101101;//5
 if (id0 == 4'd6) id0_flow=7'b1111101;//6
 if (id0 == 4'd7) id0_flow=7'b0100111;//7
 if (id0 == 4'd8) id0_flow=7'b1111111;//8
 if (id0 == 4'd9) id0_flow=7'b1100111;//9
   
 if (id1 == 4'd0) id1_flow=7'b0111111; 
 if (id1 == 4'd1) id1_flow=7'b0000110; 
 if (id1 == 4'd2) id1_flow=7'b1011011; 
 if (id1 == 4'd3) id1_flow=7'b1001111; 
 if (id1 == 4'd4) id1_flow=7'b1100110; 
 if (id1 == 4'd5) id1_flow=7'b1101101;  
 if (id1 == 4'd6) id1_flow=7'b1111101; 
 if (id1 == 4'd7) id1_flow=7'b0100111; 
 if (id1 == 4'd8) id1_flow=7'b1111111; 
 if (id1 == 4'd9) id1_flow=7'b1100111; 
     
 if (remain0 == 4'd0) remain0_flow=7'b0111111; 
 if (remain0 == 4'd1) remain0_flow=7'b0000110; 
 if (remain0 == 4'd2) remain0_flow=7'b1011011; 
 if (remain0 == 4'd3) remain0_flow=7'b1001111; 
 if (remain0 == 4'd4) remain0_flow=7'b1100110; 
 if (remain0 == 4'd5) remain0_flow=7'b1101101; 
 if (remain0 == 4'd6) remain0_flow=7'b1111101; 
 if (remain0 == 4'd7) remain0_flow=7'b0100111; 
 if (remain0 == 4'd8) remain0_flow=7'b1111111; 
 if (remain0 == 4'd9) remain0_flow=7'b1100111; 
 
 if (remain1 == 4'd0) remain1_flow=7'b0111111;
 if (remain1 == 4'd1) remain1_flow=7'b0000110;
 if (remain1 == 4'd2) remain1_flow=7'b1011011;
 if (remain1 == 4'd3) remain1_flow=7'b1001111;
 if (remain1 == 4'd4) remain1_flow=7'b1100110;
 if (remain1 == 4'd5) remain1_flow=7'b1101101;
 if (remain1 == 4'd6) remain1_flow=7'b1111101;
 if (remain1 == 4'd7) remain1_flow=7'b0100111;
 if (remain1 == 4'd8) remain1_flow=7'b1111111;
 if (remain1 == 4'd9) remain1_flow=7'b1100111;
 
 end
end
 parameter period = 200000;
  parameter period_flow = 100000000;
 
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
 
  always @(posedge clk or negedge rst)
 begin
   if(!rst) begin
     cnt_flow<=0;
     clkout_flow<=0;
   end
   else begin
      if(cnt_flow == (period_flow>>1)-1)
      begin
       clkout_flow<=~clkout_flow;
       cnt_flow<=0;
   end
   else
     cnt_flow<=cnt_flow+1;
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

always @(posedge clkout_flow or negedge rst)
begin
    if(!rst) 
    begin
     x0<= 7'b0000000;
     x1 <=7'b0000000;
     x2 <=7'b0000000;
     x3 <=7'b0000000;
     x4 <=7'b0000000;
     x5 <=7'b0000000;
     x6 <=7'b0000000;
     x7 <=7'b0000000; 
     judge<=0;
     end
    else begin
      if(judge==4'd0) begin
                      x0 <=7'b0000110;//I
                      x1 <=7'b0000000;
                      x2 <=7'b0000000;
                      x3 <=7'b0000000;
                      x4 <=7'b0000000;
                      x5 <=7'b0000000;
                      x6 <=7'b0000000;
                      x7 <=7'b0000000;
                      judge  <= judge+1; 
                      end
     else  if(judge==4'd1) begin
                       x0 <=7'b1011110;//d
                       x1 <=7'b0000110;//I   
                       x2 <=7'b0000000;
                       x3 <=7'b0000000;
                       x4 <=7'b0000000;
                       x5 <=7'b0000000;
                       x6 <=7'b0000000;
                       x7 <=7'b0000000;
                       judge  <= judge+1; 
                       end
     else    if(judge==4'd2) begin
                        x0 <= id1_flow;
                        x1 <= 7'b1011110;//d 
                        x2 <= 7'b0000110;//I 
                        x3 <=7'b0000000;
                        x4 <=7'b0000000;
                        x5 <=7'b0000000;
                        x6 <=7'b0000000;
                        x7 <=7'b0000000;
                        judge  <= judge+1; 
                        end
    else    if(judge==4'd3) begin
                         x0 <= id0_flow;
                         x1 <= id1_flow;       
                         x2 <= 7'b1011110;//d  
                         x3 <= 7'b0000110;//I  
                         x4 <=7'b0000000;
                         x5 <=7'b0000000;
                         x6 <=7'b0000000;
                         x7 <=7'b0000000;
                         judge  <= judge+1; 
                        end
      else  if(judge==4'd4) begin
                         x0 <=7'b0000000;
                         x1 <=id0_flow;       
                         x2 <=id1_flow;       
                         x3 <=7'b1011110;//d  
                         x4 <=7'b0000110;//I  
                         x5 <=7'b0000000;
                         x6 <=7'b0000000;
                         x7 <=7'b0000000;  
                         judge  <= judge+1;                   
                         end 
     else   if(judge==4'd5) begin
                         x0 <= 7'b0111000;// L
                         x1 <= 7'b0000000;    
                         x2 <= id0_flow;      
                         x3 <= id1_flow;      
                         x4 <= 7'b1011110;//d 
                         x5 <= 7'b0000110;//I 
                         x6 <=7'b0000000;
                         x7 <=7'b0000000;  
                         judge  <= judge+1;                   
                         end
     else   if(judge==4'd6) begin
                        x0 <= remain1_flow; 
                        x1 <= 7'b0111000;// L
                        x2 <= 7'b0000000;    
                        x3 <= id0_flow;      
                        x4 <= id1_flow;      
                        x5 <= 7'b1011110;//d 
                        x6 <= 7'b0000110;//I 
                        x7 <=7'b0000000;  
                        judge  <= judge+1;                  
                        end
     else    if(judge==4'd7) begin
                       x0 <= remain0_flow; 
                       x1 <= remain1_flow;  
                       x2 <= 7'b0111000;// L
                       x3 <= 7'b0000000;    
                       x4 <= id0_flow;      
                       x5 <= id1_flow;      
                       x6 <= 7'b1011110;//d 
                       x7 <= 7'b0000110;//I   
                       judge  <= judge+1;                 
                        end 
   else   if(judge==4'd8) begin
                       x0 <=7'b0000000;
                       x1 <=remain0_flow;  
                       x2 <=remain1_flow;  
                       x3 <=7'b0111000;// L
                       x4 <=7'b0000000;    
                       x5 <=id0_flow;      
                       x6 <=id1_flow;      
                       x7 <=7'b1011110;//d 
                       judge  <= judge+1;   
                       end
    else    if(judge==4'd9) begin
                        x0 <=7'b0000000;                                
                        x1 <=7'b0000000;                    
                        x2 <= remain0_flow;                 
                        x3 <= remain1_flow;                    
                        x4 <=7'b0111000;// L               
                        x5 <=7'b0000000;                   
                        x6 <=id0_flow;                        
                        x7 <= id1_flow;       
                        judge  <= judge+1;                                  
                         end 
      else   if(judge==4'd10) begin                                       
                          x0 <=7'b0000000;
                          x1 <=7'b0000000;                             
                          x2 <=7'b0000000;                            
                          x3 <= remain0_flow;                      
                          x4 <= remain1_flow;                         
                          x5 <=7'b0111000;// L                     
                          x6 <=7'b0000000;                         
                          x7 <=id0_flow;        
                          judge  <= judge+1;                                                  
                          end 
     else     if(judge==4'd11) begin                             
                            x0 <=7'b0000000;
                            x1 <=7'b0000000;                  
                            x2 <=7'b0000000;                  
                            x3 <=7'b0000000;                  
                            x4 <= remain0_flow;              
                            x5 <= remain1_flow;                 
                            x6 <=7'b0111000;// L             
                            x7 <=7'b0000000;     
                            judge  <= judge+1;                                
                           end  
     else    if(judge==4'd12) begin    
                           x0 <=7'b0000000;                           
                           x1 <=7'b0000000;                   
                           x2 <=7'b0000000;                   
                           x3 <=7'b0000000;                   
                           x4 <=7'b0000000;                   
                           x5 <= remain0_flow;                
                           x6 <= remain1_flow;                   
                           x7 <=7'b0111000;// L 
                           judge  <= judge+1;                             
                          end 
     else    if(judge==4'd13) begin
                           x0 <=7'b0000000;             
                           x1 <=7'b0000000; 
                           x2 <=7'b0000000; 
                           x3 <=7'b0000000; 
                           x4 <=7'b0000000; 
                           x5 <=7'b0000000; 
                           x6 <= remain0_flow; 
                           x7 <= remain1_flow; 
                           judge  <= judge+1; 
                          end                 
      else   if(judge==4'd14) begin
                           x0 <=7'b0000000;              
                           x1 <=7'b0000000; 
                           x2 <=7'b0000000; 
                           x3 <=7'b0000000; 
                           x4 <=7'b0000000; 
                           x5 <=7'b0000000; 
                           x6 <=7'b0000000; 
                           x7 <=  remain0_flow; 
                           judge  <= judge+1; 
                          end                                                                                                                                                                          
     else    if(judge==4'd15)  begin
                            x0 <=7'b0000110;//I  
                            x1 <=7'b0000000; 
                            x2 <=7'b0000000; 
                            x3 <=7'b0000000; 
                            x4 <=7'b0000000; 
                            x5 <=7'b0000000; 
                            x6 <=7'b0000000; 
                            x7 <=7'b0000000; 
                            judge<=0;
                           end 
             
 end
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
