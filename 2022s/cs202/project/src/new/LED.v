`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2022/05/20 22:34:05
// Design Name: 
// Module Name: LED
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


module LED(clk, rst, ledwrite, led, ledaddr,ledwdata, ledout);
    input clk;    		        // clk signal
    input rst; 		            
    input ledwrite;		       	// write signal
    input led;		         	// led signal from memoio
    input[1:0] ledaddr;	        // led address
    input[15:0] ledwdata;	  	// write data
    output[23:0] ledout;		// output data
  
    reg [23:0] ledout;
    
    always@(posedge clk, posedge rst) 
    begin
        if(rst) 
            ledout <= 24'h000000;
		else if(led && ledwrite) 
            begin
			    if(ledaddr == 2'b00)
				    ledout[23:0] <= { ledout[23:16], ledwdata[15:0] };
			    else if(ledaddr == 2'b10 )
				    ledout[23:0] <= { ledwdata[7:0], ledout[15:0] };
			    else
				    ledout <= ledout;
            end
		else 
            begin
                ledout <= ledout;
            end
    end
endmodule
