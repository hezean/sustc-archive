`timescale 1ns / 1ps

module musicplayer(
           input clk,  // 10MHz
           input music_sel,
           input music_en,
           output reg music_frac_ext = 0
       );

parameter SONG_1_LEN = 93;
parameter SONG_1_MON = 'b00011010100101010001010101000101001100010100010001010001000100111100010011010001001011000100110100010011110001000111000100011100010010010001000111000100010100010000110001000011000100111100010011010001001011000100110100010011110001000111000100011010100101010001010101000101001100010100010001010001000100111100010011010001001011000100110100010011110001000111000100011100010001010001000110101001100011101000100011010010110001110100010010100111010100110001110100011001100000;

parameter SONG_2_LEN = 76;
parameter SONG_2_MON = 'b00110010000100101001000000100101010010110101100000010110110001010010100000001001010000100001001000000011001000010010100100000010010101001011010110000001011011000101001010000000100101000010010000000110010000100101001000000100101011011000110000000011000110101110011100000001101011000110101001000000000001001010100101101011011000110101001000000100101011010100101001001010000100100000;

parameter SUCCESS_PAID_LEN = 3;
parameter SUCCESS_PAID = 'b010000100101010;

parameter FAIL_PAID_LEN = 2;
parameter FAIL_PAID = 'b0100101000;


parameter stop = 0;

parameter do_lo = 38223;
parameter re_lo = 34053;
parameter me_lo = 30337;
parameter fa_lo = 28635;
parameter so_lo = 25510;
parameter la_lo = 22727;
parameter si_lo = 20247;

parameter do = 19111;
parameter re = 17026;
parameter me = 15168;
parameter fa = 14317;
parameter so = 12755;
parameter la = 11363;
parameter si = 10123;

parameter do_hi = 9555;
parameter re_hi = 8513;
parameter me_hi = 7584;
parameter fa_hi = 7158;
parameter so_hi = 6377;
parameter la_hi = 5681;
parameter si_hi = 5061;

parameter pai_gap = 25000000;  //0.5 sec

reg [2000:0] song_midi;
reg [9:0] song_len;
reg [25:0] paicg = 0;
integer freq;
integer paisp = 0;

// wire clkdiv;
// freq_div_even#(10, 4) fd1(clk, 0, clkdiv);  // FIXME

reg music_rep = 1;


// 切换音乐
always @ (posedge clk)
case (music_sel)
    default:begin
        song_midi <= 0;
        song_len <= 0;
        music_rep <= 0;
    end
    1:begin
        song_midi <= SONG_1_MON;
        song_len <= SONG_1_LEN;
        music_rep <= 1;
    end
    2:begin
        song_midi <= SONG_2_MON;
        song_len <= SONG_2_LEN;
        music_rep <= 1;
    end
    3:begin
        song_midi <= SUCCESS_PAID;
        song_len <= SUCCESS_PAID_LEN;
        music_rep <= 0;
    end
    4:begin
        song_midi <= FAIL_PAID;
        song_len <= FAIL_PAID_LEN;
        music_rep <= 0;
    end
endcase


// @needs: song_midi, paisp
// 控制每个时间的频�?
always @ (paisp)begin
    if (!music_en)begin
        freq = stop;
    end
    else begin
        case(song_midi[paisp * 5 +:5])
            'd1 :
                freq = do_lo;
            'd2 :
                freq = re_lo;
            'd3 :
                freq = me_lo;
            'd4 :
                freq = fa_lo;
            'd5 :
                freq = so_lo;
            'd6 :
                freq = la_lo;
            'd7 :
                freq = si_lo;
            'd8 :
                freq = do;
            'd9 :
                freq = re;
            'd10:
                freq = me;
            'd11:
                freq = fa;
            'd12:
                freq = so;
            'd13:
                freq = la;
            'd14:
                freq = si;
            'd15:
                freq = do_hi;
            'd16:
                freq = re_hi;
            'd17:
                freq = me_hi;
            'd18:
                freq = fa_hi;
            'd19:
                freq = so_hi;
            'd20:
                freq = la_hi;
            'd21:
                freq = si_hi;
            default:
                freq = stop;
        endcase
    end
end

reg [18:0] freq_cnt;
// 蜂鸣器信号震�?
always @ (posedge clk)begin
    // always @ (posedge clkdiv)begin
    if(music_en)begin
        if (freq_cnt >= freq)begin
            freq_cnt = 0;
            music_frac_ext = ~music_frac_ext;
        end
        else
            freq_cnt = freq_cnt + 1;
    end
end

// 播放音乐
always @ (posedge clk)begin
    if (music_en)begin
        if(paicg >= pai_gap)begin
            paicg <= 0;
            if (paisp == 0)begin
                if(music_rep)
                    paisp = song_len;
            end
            else
                paisp = paisp - 1;
        end
        else begin
            paicg = paicg + 1;
        end
    end
end
endmodule
