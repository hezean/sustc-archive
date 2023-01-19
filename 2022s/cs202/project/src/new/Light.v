module Light(clk, rst, lightwrite, light, lightwdata, DIG, Y);
input clk; //时钟信号
input rst; //复位信号
input lightwrite; //写信号
input light; //从memorio来的LED片选信号 
input[15:0] lightwdata; //要写到数码管的data, 共3位， 每位5bit， 能显示0-9a-w

output reg[7:0] DIG; //向板子上输出的信号
output reg[7:0] Y;

reg clkout;
reg [31:0] cnt;
reg [2:0] scan_cnt;
reg [6:0] x0;
reg [6:0] x1;

wire[5:0] x0_in;
wire[5:0] x1_in;
wire[5:0] zero;

assign x0_in = lightwdata[5:0];
assign x1_in = lightwdata[11:6];

always @(*)begin
    if (rst)begin
        x0 = 7'b0;
        x1 = 7'b0;
    end
    else begin
        if(light && lightwrite)begin
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
          else if (x0_in == 6'b111_111) x0=7'b0000000;//�ո�
  
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
          else if (x1_in ==6'b111_111) x1=7'b0000000;//�ո�
        end
    end
end

parameter period = 200000;
 
 reg[6:0] Y_r;
 reg [7:0] DIG_r;
 assign Y= {1'b1,(~Y_r[6:0])};
 assign DIG= ~DIG_r;

 
 always @(posedge clk or negedge rst)
 begin
   if(rst) begin
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
 if(rst)
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
    if(light && lightwrite)begin
        case(scan_cnt)
            0:Y_r= x0;
            1:Y_r= x1;
            2:Y_r= 7'b0000000;
            3:Y_r= 7'b0000000;
            4:Y_r= 7'b0000000;
            5:Y_r= 7'b0000000;
            6:Y_r= 7'b0000000;
            7:Y_r= 7'b0000000;
            default : Y_r= 7'b0000000;
            endcase
    end
    else begin
        Y_r = 7'b0000000;
    end
 end

endmodule