module FSM (
    button, clk2, clk3, enable, key, finish, displayMode, left,st, per, id0, id1, remain0, remain1, x0, x1, x2, x3, x4, x5, x6, x7,music_sel
);
input[4:0] button;
input clk2,clk3, enable;
input [29:0] key;//键盘输入
input finish;
output reg [1:0] displayMode;
output [2:0] left;
output reg [3:0] st;
output reg [3:0] per;
output reg [3:0] id1;
output reg [3:0] id0;
output reg [3:0] remain1;
output reg [3:0] remain0;
output reg [5:0] x7;
output reg [5:0] x6;
output reg [5:0] x5;
output reg [5:0] x4;
output reg [5:0] x3;
output reg [5:0] x2;
output reg [5:0] x1;
output reg [5:0] x0;
//output [5:0] state;
output reg[2:0] music_sel;

parameter Idle = 6'b000001;
parameter InputVip = 6'b000010;
parameter Register = 6'b000011;
parameter VipRecharge = 6'b000100;
parameter VipInterface = 6'b000101;
parameter WriteOff = 6'b000110;
parameter ResetCode1 = 6'b000111;
parameter ResetCode2 = 6'b001000;
parameter InputMoney = 6'b001001;
parameter VipChargeLogin = 6'b001010;
parameter VipCharge = 6'b001011;
parameter InputId = 6'b001100;
parameter AdminInterface = 6'b001101;
parameter ShowIncome = 6'b001110;
parameter ShowState = 6'b001111;
parameter InputStallNum = 6'b010000;
parameter ShowIdA = 6'b010001;
parameter ShowNoStall = 6'b010010;
parameter ShowNoEnoughMoney = 6'b010011;
parameter ShowChange = 6'b010100;
parameter SetCharge = 6'b010101;
parameter SetVipStart = 6'b010110;
parameter Init = 6'b000000;
parameter SelectSec = 6'b010111;
parameter ShowIdB = 6'b011000;
parameter InputVipCode = 6'b011001;
parameter RegisterCode = 6'b011010;
parameter SetHour = 6'b011011;
parameter VipChargeLoginCode = 6'b011100;
parameter ShowRepeatId = 6'b011101;

parameter delay = 'd2_000;


parameter buttonUp = 5'b00001;
parameter buttonDown = 5'b00010;
parameter buttonLeft = 5'b00100;//默认为�'认键
parameter buttonRight = 5'b01000;//默认为返回键
parameter buttonMid = 5'b10000;//默认为重置键


reg [5:0] currentState;
reg [5:0] nextState = Idle;
reg [31:0] id = 32'b0;//标识码默�?????????2位，�?????????多允�?????????4辆车进入
reg [31:0] vipId = 32'b0;//vip账号默认2位，�?????????多储�?????????4�?????????
reg [31:0] vipCode = 32'b0;//vip密码默认2�?????????
reg [31:0] vipBalance = 32'b0;//vip余额
reg [31:0] timeStart = 32'b0;//每个车起始停留时间，默认8�?????????
reg [3:0] timeDuration;//要离场的车停留的总时�?????????
reg [7:0] start = 8'b00000001;
reg [7:0] vipStart = 8'b00000000;
reg [7:0] start_ ;////
reg [7:0] hour = 8'b00000001;
reg [7:0] Income;//总收�?????????
reg [15:0] counter;//倒计时，限制�?????????10s
reg [4:0] flag;//用户标记
reg [3:0] stallAble = 4'b1111;
reg [4:0] vipFlag;//vip标记
reg [7:0] clock;//现在时间
reg [7:0] duration;//停留时间
reg [7:0] change;
reg [0:0] isVip;
reg [7:0] codeCache;
reg [29:0] keycache;
wire[7:0] bill;
wire clk3;
wire clk2;

//计算价格
CountMoney coun(duration,start_,hour,bill);
//assign state = currentState;
assign left = ((id[7:0] == 8'b0) && stallAble[3]) + ((id[15:8] == 8'b0) && stallAble[2]) + ((id[23:16] == 8'b0) && stallAble[1]) + ((id[31:24] == 8'b0) && stallAble[0]);
//计时
always @(posedge clk2 or negedge enable) begin
    if (!enable) begin
        clock <= 8'b0;
    end
    else begin
        clock <= clock + 1'b1;
    end
end


//更新状�?�机
always @(posedge clk3 or negedge enable) begin
   if (!enable)begin
       currentState <= Idle;
   end
   else begin
       currentState <= nextState;
   end
end


//状�?�机切换 
always @(*) begin
    begin
        case (currentState)
            Idle: begin//闲置状�??
                // need module 设置滚动显示�?????????启，显示收费标准和剩余车�?????????
                case (button)//哪个按钮触发�?????????
                    buttonLeft: 
                        if (left != 3'b0) begin
                            nextState = SelectSec;//显示Id
                        end
                        else begin
                            nextState = ShowNoStall;//显示车位不够
                        end
                    buttonRight: nextState = InputId;
                    buttonUp: nextState = InputVip;
                    buttonDown: nextState = AdminInterface;
                    default: nextState = Idle;
                endcase
            end
            SelectSec: begin
                case(button)
                    buttonLeft: begin
                        if (((id[7:0] == 8'b0) && stallAble[3]) || ((id[15:8] == 8'b0) && stallAble[2])) begin
                            nextState = ShowIdA;
                        end
                        else begin
                            nextState = ShowNoStall;
                        end
                    end
                    buttonRight: begin
                        if (((id[31:24] == 8'b0) && stallAble[0]) || ((id[23:16] == 8'b0) && stallAble[1])) begin
                            nextState = ShowIdB;
                        end
                        else begin
                            nextState = ShowNoStall;
                        end
                    end
                    default: nextState = SelectSec;
                endcase
            end
            ShowIdA: begin
                case (button)
                    buttonLeft: nextState = Idle;
                    buttonRight: nextState = Idle;
                    default: nextState = ShowIdA;
                endcase
            end
            ShowIdB: begin
                case (button)
                    buttonLeft: nextState = Idle;
                    buttonRight: nextState = Idle;
                    default: nextState = ShowIdB;
                endcase
            end
            ShowNoStall: begin
                case (button)
                    buttonLeft: nextState = Idle;
                    buttonRight: nextState = Idle; 
                    default: nextState = ShowNoStall;
                endcase
            end
            //////////////////////////////Vip部分//////////////////////////////
            InputVip: begin
               case (button)
                    buttonLeft:begin
                        if (({keycache[9:6],keycache[3:0]} == vipId[7:0]) || ({keycache[9:6],keycache[3:0]} == vipId[15:8]) || ({keycache[9:6],keycache[3:0]} == vipId[23:16]) || ({keycache[9:6],keycache[3:0]} == vipId[31:24])) begin
                            nextState = InputVipCode;
                        end
                        else begin
                            nextState = Idle;
                        end
                    end 
                   buttonRight: nextState = Idle;
                   buttonUp: if ((vipId[7:0] == 8'b00000000) || (vipId[15:8] == 8'b00000000) || (vipId[23:16] == 8'b00000000) || (vipId[31:24] == 8'b00000000)) begin
                       nextState = Register;
                   end
                   else begin
                       nextState = InputVip;
                   end
                   default: nextState = InputVip;
               endcase
            end//finish
            InputVipCode: begin
               case (button)
                   buttonLeft: begin
                       if (vipCode[vipFlag +: 8] == {keycache[9:6],keycache[3:0]}) begin
                           nextState = VipInterface;
                       end
                       else begin
                           nextState = Idle;
                       end
                   end
                   buttonRight: nextState = Idle;
                   default: nextState = InputVipCode;
               endcase
            end//finish
            Register: begin
               case (button)
                   buttonLeft: begin
                       if (({keycache[9:6],keycache[3:0]} == vipId[7:0]) || ({keycache[9:6],keycache[3:0]} == vipId[15:8]) || ({keycache[9:6],keycache[3:0]} == vipId[23:16]) || ({keycache[9:6],keycache[3:0]} == vipId[31:24])) begin
                           nextState = ShowRepeatId;
                       end
                       else begin
                           nextState = RegisterCode;
                       end
                   end 
                   buttonRight:begin
                       nextState = Idle;
                   end 
                   default: nextState = Register;
               endcase
            end//finish
            ShowRepeatId: begin
                case (button)
                    buttonLeft: nextState = Idle;
                    buttonRight: nextState = Idle; 
                    default: nextState = ShowRepeatId;
                endcase
            end
            RegisterCode: begin
                case (button)
                    buttonLeft: begin
                        nextState = VipRecharge;
                    end 
                    buttonRight: begin
                        nextState = Idle;
                    end
                    default: nextState = RegisterCode;
                endcase
            end//finish
            VipRecharge: begin
                case (button)
                   buttonLeft: begin
                       nextState = VipInterface;
                   end
                   buttonRight: nextState = VipInterface;
                   default: nextState = VipRecharge;
               endcase
            end//finish
           VipInterface: begin
               //need module 显示余额
               case (button)
                   buttonRight: nextState = Idle;
                   buttonUp: nextState = VipRecharge;
                   buttonDown: nextState = ResetCode1;
                   buttonMid: nextState = WriteOff;
                   default: nextState = VipInterface;
               endcase
           end//finish
           WriteOff: begin
               case (button)
                   buttonLeft: begin
                       nextState = Idle;
                   end
                   buttonRight: begin
                       nextState = VipInterface;
                   end
                   default: nextState = WriteOff;
               endcase
           end//finish
           ResetCode1: begin
                case (button)
                   buttonLeft: begin
                       nextState = ResetCode2;
                   end
                   buttonRight: nextState = VipInterface;
                   default: nextState = ResetCode1;
                endcase
           end//finish
           ResetCode2: begin
               case (button)
                   buttonLeft: begin
                       if ({keycache[9:6],keycache[3:0]} == codeCache)begin
                           nextState = VipInterface;
                       end
                       else begin
                           nextState = VipInterface;
                       end
                   end
                   buttonRight: nextState = VipInterface;
                   default: nextState = ResetCode2;
               endcase
           end//finish
           //////////////////////////////////////////////////////////////////////////
           InputMoney: begin//倒计�?????????10s
               if (counter == delay) begin
                   nextState = Idle;
                end
                else begin
                    case (button)
                       buttonLeft: 
                           if(!isVip)begin
                               if ({keycache[9:6],keycache[3:0]} >= bill) begin//够钱
                                   nextState = ShowChange;
                               end
                               else begin
                                   nextState = ShowNoEnoughMoney;
                               end
                           end
                           else begin
                                if (bill < vipBalance[vipFlag +: 8]) begin
                                    nextState = ShowChange;
                                end
                                else if ({keycache[9:6],keycache[3:0]} >= bill - vipBalance[vipFlag +: 8]) begin//够钱
                                    nextState = ShowChange;
                                end
                                else begin
                                    nextState = ShowNoEnoughMoney;
                                end
                           end
                       buttonRight:begin
                           nextState = Idle;
                       end 
                       buttonUp:begin
                           nextState = VipChargeLogin;
                       end 
                       default: nextState = InputMoney;
                   endcase
               end
           end//finish
           ShowNoEnoughMoney: begin
               case (button)
                   buttonLeft:begin
                       nextState = InputMoney;
                   end 
                   buttonRight: begin
                       nextState = InputMoney;
                   end
                   default: nextState = ShowNoEnoughMoney;
               endcase
           end//finish
            ShowChange: begin
                case (button)
                    buttonLeft: nextState = Idle;
                    buttonRight: nextState = Idle; 
                    default: nextState = ShowChange;
                endcase
            end//finish
            VipChargeLogin: begin
                case (button)
                    buttonLeft: begin
                        if (({keycache[9:6],keycache[3:0]} == vipId[7:0]) || ({keycache[9:6],keycache[3:0]} == vipId[15:8]) || ({keycache[9:6],keycache[3:0]} == vipId[23:16]) || ({keycache[9:6],keycache[3:0]} == vipId[31:24])) begin
                            nextState = VipChargeLoginCode;
                        end
                        else begin
                            nextState = InputMoney;
                        end
                    end
                    buttonRight:begin
                        nextState = InputMoney; 
                    end 
                    default: nextState = VipChargeLogin;
                endcase
            end//finish
            VipChargeLoginCode: begin
                case (button)
                    buttonLeft: begin
                        if ({keycache[9:6],keycache[3:0]} == vipCode[vipFlag +: 8]) begin
                           nextState = InputMoney;
                        end 
                    end 
                    buttonRight: begin
                       nextState = InputMoney;
                    end
                    default: nextState = VipChargeLoginCode;
                endcase
           end//finish
           InputId: begin
               case (button)
                   buttonLeft:begin
                       if ((({keycache[9:6],keycache[3:0]} == id[7:0]) || ({keycache[9:6],keycache[3:0]} == id[15:8]) || ({keycache[9:6],keycache[3:0]} == id[23:16]) || ({keycache[9:6],keycache[3:0]} == id[31:24]))) begin
                           nextState = InputMoney;
                       end
                       else begin
                           nextState = Idle;
                       end
                   end
                   buttonRight: nextState = Idle;
                   default: nextState = InputId;
               endcase
           end//finish
           //////////////////////////////管理员界面部�?????????//////////////////////////////
            AdminInterface: begin
               case (button)
                   buttonRight: nextState = Idle;
                   buttonUp: nextState = ShowIncome;
                   buttonDown: nextState = ShowState;
                   default: nextState = AdminInterface;
               endcase
           end
           ShowIncome: begin
               //need module 设置显示总收�?????????
               case (button)
                    buttonRight: nextState = AdminInterface;
                    buttonUp: nextState = SetCharge;
                    buttonDown: nextState = SetVipStart;
                    buttonMid: nextState = ShowIncome;
                    default: nextState = ShowIncome;
               endcase
           end
           SetCharge: begin
                case (button)
                   buttonLeft: nextState = SetHour;
                   buttonRight: nextState = ShowIncome;
                   default: nextState = SetCharge;
                endcase
           end
           SetHour: begin
                case (button)
                   buttonLeft: nextState = ShowIncome;
                   buttonRight: nextState = ShowIncome;
                   default: nextState = SetHour;
                endcase
           end
           SetVipStart: begin
               case (button)
                    buttonLeft: begin
                        nextState = ShowIncome;
                    end
                   buttonRight: nextState = ShowIncome;
                   default: nextState = SetVipStart;
               endcase
           end
           ShowState: begin
               //need module 设置显示停车场状态，车位数，目前车辆，平均停留时�?????????
               case (button)
                   buttonRight: nextState = AdminInterface;
                   buttonUp: nextState = InputStallNum;
                   default: nextState = ShowState;
               endcase
           end
           InputStallNum: begin
               case (button)
                   buttonLeft:begin
                       if ((keycache[2:0] >= 'b100 - left) && (3'b100 >= keycache[2:0]) && (keycache[2:0] != 'b000))
                       nextState = ShowState;
                   end 
                   buttonRight: nextState = ShowState;
                   default: nextState = InputStallNum;
               endcase
           end
            /////////////////////////////////////////////////////////////////////
        endcase
    end
end


//输出�????????
always @(posedge clk3 or negedge enable) begin
    if (!enable) begin
        displayMode <= 'b0;
        id <= 32'b0;//标识码默�?????????2位，�?????????多允�?????????4辆车进入
        vipId <= 32'b0;//vip账号默认2位，�?????????多储�?????????4�?????????
        vipCode <= 32'b0;//vip密码默认2�?????????
        vipBalance <= 32'b0;//vip余额
        timeStart <= 32'b0;//每个车起始停留时间，默认8�?????????
        timeDuration <= 4'b0;//要离场的车停留的总时�?????????
        start <= 8'b00000001;
        vipStart <= 8'b00000000;
        hour <= 8'b00000001;
        Income <= 8'b0;//总收�?????????
        counter <= 16'b0;//倒计时，限制�?????????10s
        flag <= 5'b0;//用户标记
        vipFlag <= 5'b0;//vip标记
        duration <= 8'b0;//停留时间
        change <= 8'b0;
        isVip <= 1'b0;
        codeCache <= 8'b0;
        music_sel <= 3'b000;
        keycache <= 30'b0;
    end
    else begin
        if (finish) begin
            keycache <= key;
        end
        case (currentState)
            Idle: begin//闲置状�??
                displayMode <= 'd1;
                st <= start;
                per <= hour;
                music_sel <= 3'b001;
            end//finish
            SelectSec: begin
                displayMode <= 3;
                x0 <= 6'b111111;
                x1 <= 'd12;
                x2 <= 'd14;
                x3 <= 'd28;
                x4 <= 6'b111111;
                x5 <= 'd21;
                x6 <= 'd14;
                x7 <= 'd28;
                music_sel <= 3'b000;
            end//finish
            ShowIdA: begin
                music_sel <= 3'b011;
                displayMode <= 3;
                x0 <= 6'b111111;
                x1 <= 6'b111111;
                x2 <= 6'b111111;
                x4 <= 6'b111111;
                x5 <= 6'b111111;
                x6 <= 'd13;
                x7 <= 'd18;
                if ((id[7:0] == 8'b0) && stallAble[3]) begin
                    x3 <= 6'b000001;
                    if (button == buttonLeft) begin
                        id[7:0] <= 8'b00000001;
                        timeStart[7:0] <= clock;
                    end
                end
                else if ((id[15:8] == 8'b0) && stallAble[2]) begin
                    x3 <= 6'b000010;
                    if (button == buttonLeft) begin
                        id[15:8] <= 8'b00000010;
                        timeStart[15:8] <= clock;
                    end
                end
            end//finish
            ShowIdB: begin
                music_sel <= 3'b011;
                displayMode <= 3;
                x0 <= 6'b111111;
                x1 <= 6'b111111;
                x2 <= 6'b111111;
                x4 <= 6'b111111;
                x5 <= 6'b111111;
                x6 <= 'd13;
                x7 <= 'd18;
                if ((id[23:16] == 8'b0) && stallAble[1]) begin
                    x3 <= 6'b000011;
                    if (button == buttonLeft) begin
                        id[23:16] <= 8'b00000011;
                        timeStart[23:16] <= clock;
                    end
                end
                else if ((id[31:24] == 8'b0) && stallAble[0]) begin
                    x3 <= 6'b000100;
                    if (button == buttonLeft) begin
                        id[31:24] <= 8'b00000100;
                        timeStart[31:24] <= clock;
                    end
                end
            end//finish
            ShowNoStall: begin
                music_sel <= 3'b100;
                displayMode <= 3;
                x0 <= 'd21;
                x1 <= 'd21;
                x2 <= 'd10;
                x3 <= 'd29;
                x4 <= 'd28;
                x5 <= 6'b111111;
                x6 <= 'd24;
                x7 <= 'd23;
            end//finish
            //////////////////////////////Vip部分//////////////////////////////
            InputVip: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 6'b111111;
                x3 <= 'd13;
                x4 <= 'd18;
                x5 <= 'd25;
                x6 <= 'd18;
                x7 <= 'd31;
                case (button)
                    buttonLeft: begin
                        if (({keycache[9:6],keycache[3:0]} == vipId[7:0])) begin
                            vipFlag <= 5'b00000;
                        end
                        else if (({keycache[9:6],keycache[3:0]} == vipId[15:8])) begin
                            vipFlag <= 5'b01000;
                        end
                        else if (({keycache[9:6],keycache[3:0]} == vipId[23:16])) begin
                            vipFlag <= 5'b10000;
                        end
                        else if (({keycache[9:6],keycache[3:0]} == vipId[31:24])) begin
                            vipFlag <= 5'b11000;
                        end
                    end
                    buttonRight: begin
                        if (vipId[7:0] == 8'b0) begin
                            vipFlag <= 5'b00000;
                        end
                        else if (vipId[15:8] == 8'b0) begin
                            vipFlag <= 5'b01000;
                        end
                        else if (vipId[23:16] == 8'b0) begin
                            vipFlag <= 5'b10000;
                        end
                        else if (vipId[31:24] == 8'b0) begin
                            vipFlag <= 5'b11000;
                        end
                    end
                endcase
                
            end//finish
            InputVipCode: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 6'b111111;
                x3 <= 6'b111111;
                x4 <= 'd14;
                x5 <= 'd13;
                x6 <= 'd24;
                x7 <= 'd12;
            end//finish
            Register: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 6'b111111;
                x3 <= 6'b111111;
                x4 <= 'd13;
                x5 <= 'd18;
                x6 <= 6'b111111;
                x7 <= 'd27;
                case (button)
                    buttonLeft: begin
                        if (({keycache[9:6],keycache[3:0]} == vipId[7:0]) || ({keycache[9:6],keycache[3:0]} == vipId[15:8]) || ({keycache[9:6],keycache[3:0]} == vipId[23:16]) || ({keycache[9:6],keycache[3:0]} == vipId[31:24])) begin
                        end
                        else begin
                            vipId[vipFlag +: 8] <= {keycache[9:6],keycache[3:0]};
                        end
                    end 
                    buttonRight:begin
                        vipId[vipFlag +: 8] <= 8'b0;
                        vipCode[vipFlag +: 8] <= 8'b0;
                   end 
                endcase
            end//finish
            ShowRepeatId: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= 6'b111111;   
                x1 <= 'd29;
                x2 <= 'd18;
                x3 <= 'd33;
                x4 <= 'd14;
                x5 <= 6'b111111;
                x6 <= 'd13;
                x7 <= 'd18;
            end
            RegisterCode: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 'd14;
                x3 <= 'd13;
                x4 <= 'd24;
                x5 <= 'd12;
                x6 <= 6'b111111;
                x7 <= 'd27;
                case (button)
                    buttonLeft: begin
                        vipCode[vipFlag +: 8] <= {keycache[9:6],keycache[3:0]};
                    end 
                    buttonRight: begin
                        vipId[vipFlag +: 8] <= 8'b0;
                        vipCode[vipFlag +: 8] <= 8'b0;
                    end
                endcase
            end//finish
            VipRecharge: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 'd14;
                x3 <= 'd16;
                x4 <= 'd27;
                x5 <= 'd10;
                x6 <= 'd17;
                x7 <= 'd12;
                case (button)
                    buttonLeft: begin
                       vipBalance[vipFlag +: 8] <= vipBalance[vipFlag +: 8] + {keycache[9:6],keycache[3:0]};
                    end
                endcase
            end//finish
            VipInterface: begin
                displayMode <= 2;
                {id0, id1} <= vipId[vipFlag +: 8];
                {remain0, remain1} <= vipBalance[vipFlag +: 8];
                music_sel <= 3'b010;
            end//finish
            WriteOff: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= 'd15;
                x1 <= 'd15;
                x2 <= 'd24;
                x3 <= 'd14;
                x4 <= 'd29;
                x5 <= 'd18;
                x6 <= 'd27;
                x7 <= 'd32;
                case (button)
                    buttonLeft: begin
                       vipId[vipFlag +: 8] <= 8'b00000000;
                       vipCode[vipFlag +: 8] <= 8'b00000000;
                       vipBalance[vipFlag +: 8] <= 8'b00000000;
                   end
               endcase
            end//finish
            ResetCode1: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 'd1;
                x3 <= 'd32;
                x4 <= 'd14;
                x5 <= 'd28;
                x6 <= 'd14;
                x7 <= 'd27;
                case (button)
                    buttonLeft: begin
                        codeCache <= {keycache[9:6],keycache[3:0]};
                    end
                endcase
            end//finish
            ResetCode2: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 'd2;
                x3 <= 'd32;
                x4 <= 'd14;
                x5 <= 'd28;
                x6 <= 'd14;
                x7 <= 'd27;
                if ((button == buttonLeft) && ({keycache[9:6],keycache[3:0]} == codeCache)) begin
                    vipCode[vipFlag +: 8] <= keycache[7:0];
                end
            end//finish
            //////////////////////////////////////////////////////////////////////////
            InputMoney: begin//倒计�?????????10s
                displayMode <= 3;
                music_sel <= 3'b000;
                x1 <= 'd21;
                x2 <= 6'b111_111;
                x3 <= (clock[5:0] - timeStart[flag +: 6]) / 'd10;
                x4 <= 'd29;
                x5 <= 6'b111_111;
                x6 <= bill[5:0]; 
                x7 <= 'd11;
                if (!isVip) begin
                    x0 <= 6'b111_111;
                end
                else begin
                    x0 <= vipBalance[vipFlag +: 6];
                end
                if (counter == delay) begin
                   counter <= 16'b0;
                end
                else begin
                    counter <= counter + 1;
                    duration <= clock - timeStart[flag +: 8]; 
                    if (button == buttonLeft) begin
                        if(!isVip)begin
                            start_ <= start;
                            if ({keycache[9:6],keycache[3:0]} >= bill) begin//够钱
                                id[flag +: 8] <= 8'b0;
                                timeStart[flag +: 8] <= 8'b0;
                                Income <= Income + bill;
                                change <= {keycache[9:6],keycache[3:0]} - bill;
                            end
                        end
                        else begin
                            start_ <= vipStart;
                            if (bill < vipBalance[vipFlag +: 8]) begin
                                id[flag +: 8] <= 8'b0;
                                vipBalance[vipFlag +: 8] <= vipBalance[vipFlag +: 8] - bill;
                                timeStart[flag +: 8] <= 8'b0;
                                Income <= Income + bill;
                                change <= 0;
                            end
                            else if({keycache[9:6],keycache[3:0]} >= bill - vipBalance[vipFlag +: 8]) begin
                                id[flag +: 8] <= 8'b0;
                                vipBalance[vipFlag +: 8] <= 8'b0;
                                timeStart[flag +: 8] <= 8'b0;
                                Income <= Income + bill;
                                change <= {keycache[9:6],keycache[3:0]} - bill;
                            end
                        end
                    end
                end
            end//finish
            ShowNoEnoughMoney: begin
                counter <= 16'b0;
                music_sel <= 3'b100;
                displayMode <= 3;
                x0 <= 'd28;
                x1 <= 'd28;
                x2 <= 'd14;
                x3 <= 'd21;
                x4 <= 6'b111111;
                x5 <= 'd24;
                x6 <= 'd24;
                x7 <= 'd29;
            end//finish
            ShowChange: begin
                displayMode <= 3;
                music_sel <= 3'b011;
                x0 <= 6'b111111;
                x2 <= 6'b111111;
                x4 <= 6'b111111;
                x5 <= change[5:0];
                x6 <= 6'b111111;
                x7 <= 'd12;
                if (!isVip) begin
                    x3 <= 6'b111111;
                    x1 <=6'b111111;
                end
                else begin
                    x3 <= 'd21;
                    x1 <= vipBalance[vipFlag +: 8];
                end
            end//finish
            VipChargeLogin: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 'd13;
                x3 <= 'd18;
                x4 <= 6'b111111;
                x5 <= 'd25;
                x6 <= 'd18;
                x7 <= 'd31;
                case (button)
                    buttonLeft: begin
                       if (({keycache[9:6],keycache[3:0]} == vipId[7:0])) begin
                            vipFlag <= 5'b00000;
                       end
                       else if (({keycache[9:6],keycache[3:0]} == vipId[15:8])) begin
                            vipFlag <= 5'b01000;
                       end
                       else if (({keycache[9:6],keycache[3:0]} == vipId[23:16])) begin
                            vipFlag <= 5'b10000;
                       end
                       else if (({keycache[9:6],keycache[3:0]} == vipId[31:24])) begin
                            vipFlag <= 5'b11000;
                       end
                    end
                    buttonRight:begin
                        counter <= 16'b0;
                    end 
                endcase
            end//finish
            VipChargeLoginCode: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 6'b111111;
                x3 <= 6'b111111;
                x4 <= 'd14;
                x5 <= 'd13;
                x6 <= 'd24;
                x7 <= 'd12;
                case (button)
                    buttonLeft: begin
                        if ({keycache[9:6],keycache[3:0]} == vipCode[vipFlag +: 8]) begin
                           isVip <= 1'b1;
                           counter <= 16'b0;
                        end 
                    end 
                    buttonRight: begin
                       counter <= 16'b0;
                    end
                endcase
            end//finish
            InputId: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 6'b111111;
                x3 <= 'd29;
                x4 <= 'd30;
                x5 <= 'd25;
                x6 <= 'd23;
                x7 <= 'd18;
                if (({keycache[9:6],keycache[3:0]} == id[7:0])) begin
                    flag <= 5'b00000;
                    isVip <= 1'b0;
                    counter <= 16'b0;
                end
                else if (({keycache[9:6],keycache[3:0]} == id[15:8]))begin
                    flag <= 5'b01000;
                    isVip <= 1'b0;
                    counter <= 16'b0;
                end
                else if (({keycache[9:6],keycache[3:0]} == id[23:16]))begin
                    flag <= 5'b10000;
                    isVip <= 1'b0;
                    counter <= 16'b0;
                end
                else if (({keycache[9:6],keycache[3:0]} == id[31:24]))begin
                    flag <= 5'b11000;
                    isVip <= 1'b0;
                    counter <= 16'b0;
                end
            end//finish
            //////////////////////////////管理员界面部�?????????//////////////////////////////
            AdminInterface: begin
                displayMode <= 3;
                music_sel <= 3'b010;
                x0 <= 6'b111111;
                x1 <= 6'b111111;
                x2 <= 6'b111111;
                x3 <= 'd23;
                x4 <= 'd18;
                x5 <= 'd22;
                x6 <= 'd13;
                x7 <= 'd10;
                if (button == buttonMid) begin
                    id <= 32'b0;
                    vipId <= 32'b0;
                    vipCode <= 32'b0;
                    vipBalance <= 32'b0;
                    timeStart <= 32'b0;
                    timeDuration <= 4'b0;
                    start <= 8'b00000010;
                    vipStart <= 8'b00000001;
                    start_ <= 8'b00000001;
                    hour <= 8'b00000001;
                    Income <= 8'b0;
                    counter <= 16'b0;
                    flag <= 5'b0;
                    vipFlag <= 5'b0;
                    duration <= 8'b0;
                    change <= 8'b0;
                    isVip <= 1'b0;
                    codeCache <= 8'b0;
                end
            end
            ShowIncome: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= Income[5:0];
                x1 <= 6'b111111;
                x2 <= 'd14;
                x3 <= 'd22;
                x4 <= 'd24;
                x5 <= 'd12;
                x6 <= 'd23;
                x7 <= 'd18;
                case (button)
                   buttonMid: begin 
                       Income <= 4'b0;
                   end
               endcase
            end
            SetCharge: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 6'b111111;
                x3 <= 'd29;
                x4 <= 'd27;
                x5 <= 'd10;
                x6 <= 'd29;
                x7 <= 'd28;
                case (button)
                   buttonLeft: start <= {keycache[9:6],keycache[3:0]};
                endcase
            end
            SetHour: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 6'b111111;
                x3 <= 6'b111111;
                x4 <= 'd27;
                x5 <= 'd30;
                x6 <= 'd24;
                x7 <= 'd17;
                case (button)
                   buttonLeft: hour <= {keycache[9:6],keycache[3:0]};
                endcase
            end
            SetVipStart: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 6'b111111;
                x3 <= 6'b111111;
                x4 <= 6'b111111;
                x5 <= 6'b111111;
                x6 <= 'd28;
                x7 <= 'd31;
                case (button)
                   buttonLeft: vipStart <= {keycache[9:6],keycache[3:0]};
                endcase
            end
            ShowState: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= 6'b111111;
                x1 <= 6'b111111;
                x2 <= ((((timeStart[5:0] != 6'b0) + (timeStart[13:8] != 6'b0) + (timeStart[21:16] != 6'b0) + (timeStart[29:24] != 6'b0)) * clock - (timeStart[5:0] + timeStart[13:8] + timeStart[21:16] + timeStart[29:24])) / ((timeStart[5:0] != 6'b0) + (timeStart[13:8] != 6'b0) + (timeStart[21:16] != 6'b0) + (timeStart[29:24] != 6'b0))) / 'd10;
                x3 <= 6'b111111;
                x4 <= 6'b111111;
                x5 <= 'd14;
                x6 <= 'd31;
                x7 <= 'd10;
            end
            InputStallNum: begin
                displayMode <= 3;
                music_sel <= 3'b000;
                x0 <= key[5:0];   
                x1 <= key[11:6];
                x2 <= 6'b111111;
                x3 <= 6'b111111;
                x4 <= 6'b111111;
                x5 <= 'd22;
                x6 <= 'd30;
                x7 <= 'd23;
                case (button)
                    buttonLeft:begin
                        if ((keycache[2:0] >= 'b100 - left) && (3'b100 >= keycache[2:0]) && (keycache[2:0] != 'b000)) begin
                            case (keycache[3:0])
                                 'b100: stallAble <= 'b1111;
                                 'b011: stallAble <= 'b1110;
                                 'b010: stallAble <= 'b1010;
                                 'b001: stallAble <= 'b1000; 
                            endcase
                        end
                    end
                endcase
            end
            ///////////////////////////////////////////////////////////////////////
            default: displayMode <= 'd0;
        endcase
    end
end
endmodule