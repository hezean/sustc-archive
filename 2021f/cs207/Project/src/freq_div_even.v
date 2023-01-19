
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
