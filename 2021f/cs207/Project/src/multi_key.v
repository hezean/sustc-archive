`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2021/12/21 16:44:41
// Design Name: 
// Module Name: multi_key
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


module multi_key(rst,clk,cnt_tmp,row,col,DIG,Y,finish,result);
    input rst;
    input clk;
    input [2:0] cnt_tmp;
    reg [2:0] cnt;
    input [3:0] row;               
    output [3:0] col;
    output [7:0] DIG;
    output [7:0] Y;
    output reg finish;
    output reg [29:0] result;
    wire [5:0] key_val;
    reg [1:0] display_rst;
    wire [5:0] x0;        
    wire [5:0] x1;        
    wire [5:0] x2;
    wire [5:0] x3;
    wire [5:0] x4;
    wire [5:0] x5;
    wire [5:0] x6;
    wire [5:0] x7;
    wire key_pressed_flag;
    wire key_clk;
    reg flag;
    reg[19:0] count;
    key_top key(clk,rst,row,col,key_val,key_clk,key_pressed_flag);
    display_top display(display_rst,clk,4'd0,4'd0,4'd0,4'd0,4'd0,4'd0,4'd0,x7,x6,x5,x4,x3,x2,x1,x0,DIG,Y);

      assign x0 = result[5:0];   
      assign x1 = result[11:6];  
      assign x2 = result[17:12]; 
      assign x3 = result[23:18]; 
      assign x4 = result[29:24]; 
      assign x5 = 6'b111_111;    
      assign x6 = 6'b111_111;    
      assign x7 = 6'b111_111;   
      
    always@(posedge key_clk )
    begin
           if(key_pressed_flag)
           count=count+1;
           
           if(count== 20'd20)
           begin
               flag=1;
               count=20'd0;
               end
               else 
               flag=0;
   end
                     
        
    always@(posedge key_clk or posedge rst) 
    if(rst)
    begin
     cnt=cnt_tmp;
     finish=0;
     result=30'b111111_111111_111111_111111_111111;
     display_rst=2'b11;
    end
    else
      begin
        if(cnt==3'd0)
          begin       
          finish=1;  
          end        
        else
        if(flag)
          begin
            finish=0;
            case(key_val)
             6'h0:begin                                   
                  result[29:24]=6'b000_000;                 
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                 end                                
             6'h1:begin                                    
                  result[29:24]=6'b000_001;                 
                  result={ result[23:0], result[29:24] };  
                   cnt=cnt-1'b1;            
                  end                                     
             6'h2:begin                                    
                  result[29:24]=6'b000_010;                 
                  result={ result[23:0], result[29:24] }; 
                   cnt=cnt-1'b1;   
                  end                                     
             6'h3:begin                                  
                  result[29:24]=6'b000_011;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'h4:begin                                  
                  result[29:24]=6'b000_100;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'h5:begin                                  
                  result[29:24]=6'b000_101;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'h6:begin                                  
                  result[29:24]=6'b000_110;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'h7:begin                                  
                     result[29:24]=6'b000_111;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'h8:begin                                  
                  result[29:24]=6'b001_000;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'h9:begin                                  
                  result[29:24]=6'b001_001;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'hA:begin                                  
                  result[29:24]=6'b001_010;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'hB:begin                                  
                  result[29:24]=6'b001_011;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'hC:begin                                  
                  result[29:24]=6'b001_100;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
             6'hD:begin                                  
                  result[29:24]=6'b001_101;              
                  result={ result[23:0], result[29:24] };
                   cnt=cnt-1'b1;   
                  end                                    
            6'hE:begin                                  
                 result[29:24]=6'b001_110;              
                 result={ result[23:0], result[29:24] };
                  cnt=cnt-1'b1;   
                 end                                    
            6'hF:begin                                  
                 result[29:24]=6'b001_111;              
                 result={ result[23:0], result[29:24] };    
                  cnt=cnt-1'b1;           
                 end                                                                                                                                                                                                                                                                                                       
            endcase           
       
          end   
     end
endmodule
