`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2022/05/20 22:34:35
// Design Name: 
// Module Name: Switch
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


module Switch(clk, rst, switchread, switch,switchaddr, switchrdata, switch_i);
    input clk;			        //  clk signal
    input rst;			        
    input switch;			        //  switch signal from memrio
    input[1:0] switchaddr;		    //  address of switch
    input switchread;			    //  read signal 
    output [15:0] switchrdata;	    //  output data from switch
    input [23:0] switch_i;		    //  read from board

    reg [15:0] switchrdata;

    always@(negedge clk, posedge rst) 
    begin
        if(rst) 
            switchrdata <= 0;
		else if(switch && switchread) 
            begin
			    if(switchaddr==2'b00)
				    switchrdata[15:0] <= switch_i[15:0];   // data output,lower 16 bits non-extended
			    else if(switchaddr==2'b10)
				    switchrdata[15:0] <= { 8'h00, switch_i[23:16] }; //data output, upper 8 bits extended with zero
			    else 
				    switchrdata <= switchrdata;
            end
		else
            switchrdata <= switchrdata;
    end
endmodule
