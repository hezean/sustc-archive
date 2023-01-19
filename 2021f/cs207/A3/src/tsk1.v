`timescale 1ns/1ps

module decode_74138(
    input A, B, C,
    input G1, G2a, G2b,
    output reg [7:0] Y
);
    always @ * begin
        if ({G1, G2a, G2b} == 'b100) begin
            case ({C, B, A})
                'd0: Y <= ~'b0000_0001;
                'd1: Y <= ~'b0000_0010;
                'd2: Y <= ~'b0000_0100;
                'd3: Y <= ~'b0000_1000;
                'd4: Y <= ~'b0001_0000;
                'd5: Y <= ~'b0010_0000;
                'd6: Y <= ~'b0100_0000;
                'd7: Y <= ~'b1000_0000;
            endcase
        end
        else begin
            Y = ~'b0000_0000;
        end
    end
endmodule


module decode_4_16(
    input A, B, C, D,
    input dec_en,
    output [15:0] Y
);
    reg [2:0] lo_en = 'b000;
    reg [2:0] hi_en = 'b000;
    decode_74138 lo(A, B, C, lo_en[2], lo_en[1], lo_en[0], Y[7:0]);
    decode_74138 hi(A, B, C, hi_en[2], hi_en[1], hi_en[0], Y[15:8]);

        always @ * begin
        if (dec_en) begin
            if (!D) begin
                lo_en <= 'b100;
                hi_en <= 'b000;
            end
            else begin
                lo_en <= 'b000;
                hi_en <= 'b100;
            end
        end
        else begin
            lo_en <= 'b000;
            hi_en <= 'b000;
        end
    end
endmodule
