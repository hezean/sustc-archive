`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2021/12/08 20:59:27
// Design Name: 
// Module Name: flowinglight
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


module flowinglight(rst,clk,DIG,Y,left,start,each);
 input rst;
 input clk;
 output [7:0] DIG;
 output  [7:0] Y;
 input [3:0] left;
 input [3:0] start;
 input [3:0] each;
 reg clkout;
 reg [31:0] cnt;
 reg clkout_flow;
 reg [31:0] cnt_flow;
 reg [2:0] scan_cnt;
 reg [6:0] left_flow;
 reg  [6:0] start_flow;
 reg [6:0] each_flow;
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
      left_flow=7'b0000000;
      start_flow=7'b0000000;
      each_flow=7'b0000000;
  end 
  else           
begin
 if (left == 4'd0) left_flow=7'b0111111;//0
 if (left == 4'd1) left_flow=7'b0000110;//1
 if (left == 4'd2) left_flow=7'b1011011;//2
 if (left == 4'd3) left_flow=7'b1001111;//3
 if (left == 4'd4) left_flow=7'b1100110;//4
 if (left == 4'd5) left_flow=7'b1101101;//5
 if (left == 4'd6) left_flow=7'b1111101;//6
 if (left == 4'd7) left_flow=7'b0100111;//7
 if (left == 4'd8) left_flow=7'b1111111;//8
 if (left == 4'd9) left_flow=7'b1100111;//9
   
 if (start == 4'd0) start_flow=7'b0111111; 
 if (start == 4'd1) start_flow=7'b0000110; 
 if (start == 4'd2) start_flow=7'b1011011; 
 if (start == 4'd3) start_flow=7'b1001111; 
 if (start == 4'd4) start_flow=7'b1100110; 
 if (start == 4'd5) start_flow=7'b1101101;  
 if (start == 4'd6) start_flow=7'b1111101; 
 if (start == 4'd7) start_flow=7'b0100111; 
 if (start == 4'd8) start_flow=7'b1111111; 
 if (start == 4'd9) start_flow=7'b1100111; 
     
 if (each == 4'd0) each_flow=7'b0111111; 
 if (each == 4'd1) each_flow=7'b0000110; 
 if (each == 4'd2) each_flow=7'b1011011; 
 if (each == 4'd3) each_flow=7'b1001111; 
 if (each == 4'd4) each_flow=7'b1100110; 
 if (each == 4'd5) each_flow=7'b1101101; 
 if (each == 4'd6) each_flow=7'b1111101; 
 if (each == 4'd7) each_flow=7'b0100111; 
 if (each == 4'd8) each_flow=7'b1111111; 
 if (each == 4'd9) each_flow=7'b1100111; 
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
                      x0 <=7'b0111000;//L
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
                       x0 <= left_flow;
                       x1 <=7'b0111000; //L
                       x2 <=7'b0000000;
                       x3 <=7'b0000000;
                       x4 <=7'b0000000;
                       x5 <=7'b0000000;
                       x6 <=7'b0000000;
                       x7 <=7'b0000000;
                       judge  <= judge+1; 
                       end
     else    if(judge==4'd2) begin
                        x0 <= 7'b0000000;
                        x1 <= left_flow; 
                        x2 <= 7'b0111000;//L
                        x3 <=7'b0000000;
                        x4 <=7'b0000000;
                        x5 <=7'b0000000;
                        x6 <=7'b0000000;
                        x7 <=7'b0000000;
                        judge  <= judge+1; 
                        end
    else    if(judge==4'd3) begin
                         x0 <= 7'b1101101;//S
                         x1 <= 7'b0000000 ; 
                         x2 <= left_flow ;
                         x3 <= 7'b0111000;//L
                         x4 <=7'b0000000;
                         x5 <=7'b0000000;
                         x6 <=7'b0000000;
                         x7 <=7'b0000000;
                         judge  <= judge+1; 
                        end
      else  if(judge==4'd4) begin
                         x0 <= start_flow;
                         x1 <= 7'b1101101;//S
                         x2 <= 7'b0000000; 
                         x3 <= left_flow ;
                         x4 <= 7'b0111000;//L
                         x5 <=7'b0000000;
                         x6 <=7'b0000000;
                         x7 <=7'b0000000;  
                         judge  <= judge+1;                   
                         end 
     else   if(judge==4'd5) begin
                         x0 <= 7'b0000000;
                         x1 <= start_flow;
                         x2 <= 7'b1101101;//S
                         x3 <= 7'b0000000 ; 
                         x4 <= left_flow ;
                         x5 <= 7'b0111000;//L
                         x6 <=7'b0000000;
                         x7 <=7'b0000000;  
                         judge  <= judge+1;                   
                         end
     else   if(judge==4'd6) begin
                        x0 <=7'b1111001; //E
                        x1 <=7'b0000000;
                        x2 <= start_flow;
                        x3 <= 7'b1101101;//S
                        x4 <= 7'b0000000 ; 
                        x5 <= left_flow ;
                        x6 <= 7'b0111000;//
                        x7 <=7'b0000000;  
                        judge  <= judge+1;                  
                        end
     else    if(judge==4'd7) begin
                       x0 <= each_flow; 
                       x1 <=7'b1111001; //E
                       x2 <=7'b0000000;
                       x3 <= start_flow;
                       x4 <= 7'b1101101;//S
                       x5 <= 7'b0000000 ; 
                       x6 <= left_flow ;
                       x7 <= 7'b0111000; //L  
                       judge  <= judge+1;                 
                        end 
   else   if(judge==4'd8) begin
                       x0 <=7'b0000000;
                       x1 <= each_flow; 
                       x2 <=7'b1111001; //E
                       x3 <=7'b0000000;
                       x4 <= start_flow;
                       x5 <= 7'b1101101;//S
                       x6 <= 7'b0000000 ; 
                       x7 <= left_flow ; 
                       judge  <= judge+1;   
                       end
    else    if(judge==4'd9) begin
                        x0 <=7'b0000000;                                
                        x1 <=7'b0000000;                    
                        x2 <= each_flow;                    
                        x3 <=7'b1111001;  //E                  
                        x4 <=7'b0000000;                    
                        x5 <= start_flow;                   
                        x6 <= 7'b1101101; //S                  
                        x7 <= 7'b0000000;   
                        judge  <= judge+1;                                  
                         end 
      else   if(judge==4'd10) begin                                       
                          x0 <=7'b0000000;
                          x1 <=7'b0000000;                             
                          x2 <=7'b0000000;                            
                          x3 <= each_flow;                            
                          x4 <=7'b1111001;  //E                          
                          x5 <=7'b0000000;                            
                          x6 <= start_flow;                           
                          x7 <= 7'b1101101;  //S   
                          judge  <= judge+1;                                                  
                          end 
     else     if(judge==4'd11) begin                             
                            x0 <=7'b0000000;
                            x1 <=7'b0000000;                  
                            x2 <=7'b0000000;                  
                            x3 <=7'b0000000;                  
                            x4 <= each_flow;                  
                            x5 <=7'b1111001;   //E               
                            x6 <=7'b0000000;                  
                            x7 <= start_flow; 
                            judge  <= judge+1;                                
                           end  
     else    if(judge==4'd12) begin    
                           x0 <=7'b0000000;                           
                           x1 <=7'b0000000;                   
                           x2 <=7'b0000000;                   
                           x3 <=7'b0000000;                   
                           x4 <=7'b0000000;                   
                           x5 <= each_flow;                   
                           x6 <=7'b1111001;  //E                 
                           x7 <=7'b0000000;  
                           judge  <= judge+1;                             
                          end 
     else    if(judge==4'd13) begin
                           x0 <=7'b0000000;             
                           x1 <=7'b0000000; 
                           x2 <=7'b0000000; 
                           x3 <=7'b0000000; 
                           x4 <=7'b0000000; 
                           x5 <=7'b0000000; 
                           x6 <= each_flow; 
                           x7 <=7'b1111001; //E
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
                           x7 <= each_flow; 
                           judge  <= judge+1; 
                          end                                                                                                                                                                          
     else    if(judge==4'd15)  begin
                            x0 <=7'b0111000; //L
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
