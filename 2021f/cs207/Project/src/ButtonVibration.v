module ButtonVibration (
    input clk,
    input rst,
    input [4:0] button,
    output  [4:0] buttonout
);
    reg[19:0] cnt0;
    reg[19:0] cnt1;
    reg[19:0] cnt2;
    reg[19:0] cnt3;
    reg[19:0] cnt4;
    
    reg flag0;
    reg flag1;
    reg flag2;
    reg flag3;
    reg flag4;
 //   wire buttonEnable;
    wire bclk;
    
    reg [19:0] count;                         // ������
    //wire key_clk;
     
    always @ (posedge clk or posedge rst)
      if (!rst )
        count <= 0;
      else 
        count <= count + 1'b1;
        
    assign bclk = count[19]; 
    
    assign buttonout={flag4,flag3,flag2,flag1,flag0};
    
 //   assign buttonEnable = button[4] || button[3] || button[2] || button[1] || button[0]; //只要任意按键被按下，相应的按键进行消�???
    
    always@(posedge bclk, negedge rst)
    begin
        if(!rst)
            cnt0 = 1'b0;
        else begin
               if(button[0])
                      cnt0=cnt0+1;
                      
                      if(cnt0== 20'd20)
                      begin
                          flag0=1;
                          cnt0=20'd0;
                          end
                          else 
                          flag0=0;
              end
    end
    
      always@(posedge bclk, negedge rst)
       begin
           if(!rst)
               cnt1 = 1'b0;
           else begin
                  if(button[1])
                         cnt1=cnt1+1;
                         
                         if(cnt1== 20'd20)
                         begin
                             flag1=1;
                             cnt1=20'd0;
                             end
                             else 
                             flag1=0;
                 end
       end


      always@(posedge bclk, negedge rst)
       begin
           if(!rst)
               cnt2 = 1'b0;
           else begin
                  if(button[2])
                         cnt2=cnt2+1;
                         
                         if(cnt2== 20'd20)
                         begin
                             flag2=1;
                             cnt2=20'd0;
                             end
                             else 
                             flag2=0;
                 end
       end
       
       
        always@(posedge bclk, negedge rst)
         begin
             if(!rst)
                 cnt3 = 1'b0;
             else begin
                    if(button[3])
                           cnt3=cnt3+1;
                           
                           if(cnt3== 20'd20)
                           begin
                               flag3=1;
                               cnt3=20'd0;
                               end
                               else 
                               flag3=0;
                   end
         end
              
              
         always@(posedge bclk, negedge rst)
          begin
              if(!rst)
                  cnt4 = 1'b0;
              else begin
                     if(button[4])
                            cnt4=cnt4+1;
                            
                            if(cnt4== 20'd20)
                            begin
                                flag4=1;
                                cnt4=20'd0;
                                end
                                else 
                                flag4=0;
                    end
          end
          
//    always @(posedge bclk, negedge rst) begin
//        if(!rst) buttonout = 5'b0;
//        else if (button[0]) buttonout[0] <= (flag) ? 1'b1 : 1'b0;
//        else if (button[1]) buttonout[1] <= (flag) ? 1'b1 : 1'b0;
//        else if (button[2]) buttonout[2] <= (flag) ? 1'b1 : 1'b0;
//        else if (button[3]) buttonout[3] <= (flag) ? 1'b1 : 1'b0;
//        else if (button[4]) buttonout[4] <= (flag) ? 1'b1 : 1'b0;
//        else buttonout <= 5'b0;
//    end
endmodule
