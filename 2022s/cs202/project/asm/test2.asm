.data 0x0000
    # pass

.text 0x0000

init:  # assign reg as pg, init memory
    lui $1, 0xFFFF
    ori $28, $1, 0xF000  # for hardware io

    add $29, $28, $0  # addr bias for ram, base = $28, reserved for dataset

    # at most 41 numbers to be stored in the ram, size (1) + data (4 * 10)
    addi $13, $zero, 41
    addi $2, $zero, 1
    init_for:
        sub $13, $13, $2
        sw $0, 0x0($29)
        addi $29, $29, 4
        bne $13, $zero, init_for


main:
    # main menu light
    ori $1, $0, 0xFFFF  
    sw $1, 0xC62($28)
    sw $0, 0xC60($28)
    # wait until user confirm
    ori $24, $zero, 0

    # press button
    m_loop1:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop1
    
    m_loop2:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop2

    lw $8, 0xC72($28)

    # get the case_num
    sll $8, $8, 24
    srl $8, $8, 29
    j switch_case


read:
    ori $24, $0, 0  # bypass compile error

    # press button
    r_loop1:
        lw $3, 0xC80($28)
        beq $3, $0, r_loop1
    
    r_loop2:
        lw $3, 0xC80($28)
        bne $3, $0, r_loop2


    lw $8, 0xC70($28) # data <- sw0

    sll $8, $8, 24
    srl $8, $8, 24  # take low 8 bits
    sw $8, 0xC60($28)
    sw $8, 0x0($29) # -> sp

    ori $24, $0, 0
    jr $31

switch_case:
    ori $23, $zero, 0  # 杈撳叆娴嬭瘯鏁版嵁鐨勪釜鏁帮紙灏忎簬鎴栫瓑浜?0锛夛紝浠?杩涘埗鐨勬柟寮忚緭鍏ュ涓祴璇曟暟鎹紝灏嗘祴璇曟暟鎹師鏍峰瓨鍏ヤ笂琛?鈥濇暟鎹泦0鈥?瀵瑰簲鐨勭┖闂?
    beq $8, $23, case1

    ori $23, $zero, 1  # 灏嗘祴璇曟暟鎹涓烘棤绗﹀彿鏁帮紝灏嗘暟鎹寜鐓т粠灏忓埌澶х殑鏂瑰紡杩涜鎺掑簭锛屾帓搴忓悗鐨勬暟鎹泦鍚堜綔涓轰竴涓暣浣撳瓨鍏ヤ笂琛?鈥濇暟鎹泦1鈥?瀵瑰簲鐨勭┖闂翠腑锛堟暟鍊兼渶灏忕殑瀛樻斁鍦ㄤ綆鍦板潃锛屼緷娆＄被鎺紝鏁板€兼渶澶х殑瀛樻斁鍦ㄩ珮鍦板潃锛?
    beq $8, $23, case2

    ori $23, $zero, 2  # 灏嗘祴璇曟暟鎹紙姝ゆ椂鈥濇暟鎹泦0鈥滀腑鐨勬瘡涓€涓暟鎹紝鍏禸it6~bit0琚涓鸿鏁板€肩殑缁濆鍊硷紝bit7瀵瑰簲璇ユ暟鍊肩殑绗﹀彿浣嶏級杞崲涓烘湁绗﹀彿鏁扮殑琛ョ爜褰㈠紡锛屽皢鍋氬畬杞崲鍚庣殑鏁版嵁闆嗗瓨鍏ヤ笂琛?鈥濇暟鎹泦2鈥?瀵瑰簲鐨勭┖闂翠腑锛堝瓨鏀炬搴忎笌鏁版嵁闆?涓殑鏁版嵁涓€鑷达級
    beq $8, $23, case3

    ori $23, $zero, 3  # 鍩轰簬鈥滄暟鎹泦2鈥濅腑鐨勬暟鎹紝瑙嗗叾涓烘湁绗﹀彿鏁扮殑琛ョ爜锛屾寜浠庡皬鍒板ぇ鎺掑簭鍚庨『搴忓瓨鏀剧殑鏁版嵁锛屾帓搴忓悗鐨勬暟鎹泦鍚堜綔涓轰竴涓暣浣撳瓨鍏ヤ笂琛ㄢ€濇暟鎹泦3鈥?瀵瑰簲鐨勭┖闂翠腑锛堟暟鍊兼渶灏忕殑瀛樻斁鍦ㄤ綆鍦板潃锛屼緷娆＄被鎺紝鏁板€兼渶澶х殑瀛樻斁鍦ㄩ珮鍦板潃锛?
    beq $8, $23, case4

    ori $23, $zero, 4  # 鐢ㄦ暟鎹泦1涓殑鏈€澶у€煎噺鍘昏鏁版嵁闆嗕腑鐨勬渶灏忓€硷紝灏嗙粨鏋滄樉绀哄湪杈撳嚭璁惧
    beq $8, $23, case5

    ori $23, $zero, 5  # 鐢ㄦ暟鎹泦3涓殑鏈€澶у€煎噺鍘昏鏁版嵁闆嗕腑鐨勬渶灏忓€硷紝灏嗙粨鏋滄樉绀哄湪杈撳嚭璁惧
    beq $8, $23, case6

    ori $23, $zero, 6  # 杈撳叆鏁版嵁闆嗙紪鍙凤紙1鎴?鎴?锛夊拰璇ユ暟鎹泦涓厓绱犵殑涓嬫爣(浠?寮€濮嬬紪鍧€)锛岃緭鍑鸿鏁版嵁鐨勪綆8bit
    beq $8, $23, case7

    ori $23, $zero, 7  # 杈撳叆鏁版嵁闆嗕腑鍏冪礌鐨勪笅鏍囷紝鍦ㄨ緭鍑鸿澶囦笂浜ゆ浛鏄剧ず涓嬮潰涓ょ粍淇℃伅锛堥棿闅?绉掞級
    beq $8, $23, case8

    j main

copy_dataset_old_start_from_s24_new_from_s25:
    lw $12, 0x($28)   # len(dataset)
    
    # copy dataset
    cdfori:
        lw $8, 0x0($24)
        sw $8, 0x0($25)

        addi $24, $24, 4
        addi $25, $25, 4

        addi $12, $12, -1
        bne $12, $0, cdfori
    jr $31


case1:
    ori $1, $0, 1  
    sw $1, 0xC62($28)
    # sp = (8-bit)*
    addi $29, $28, 0

    jal read  # place number of data in a dataset to gp[0]

    lw $15, 0x0($29)  # get the number of data -> $t7
    sw $15, 0x62($28)


    read_all_data:
        addi $29, $29, 4  # bias+32, use standard 32 bit to store 8bit data
        jal read  # place number of data in a dataset to sp[0]

        addi $15, $15, -1
        bne $15, $0, read_all_data

    # press enter to exit
    c1_loop1:
        lw $3, 0xC80($28)
        beq $3, $0, c1_loop1
    
    c1_loop2:
        lw $3, 0xC80($28)
        bne $3, $0, c1_loop2

    j main

case2:
    # disp case 2
    ori $1, $0, 2  
    sw $1, 0xC62($28)

    lw $12, 0x($28)
    sll $12, $12, 2    # sizeof(dataset)

    addi $24, $28, 4  # start of ds0
    add $25, $24, $12  # start of ds1

    jal copy_dataset_old_start_from_s24_new_from_s25

    ori $10, $zero, 4  # i
    lw $12, 0x($28)
    sll $12, $12, 2    # sizeof(dataset)

    addi $3, $12, -4  # 4 * (len-1)
    beq $3, $0, skpc2  # only 1 data, no need to sort


    c2fori:
        beq $10, $12, c2jx  # i < len

        ori $11, $zero, 0  # j
        sub $13, $12, $10  # len - i

        c2forj:
            # j < len - i
            beq $11, $13, c2ix

            addi $14, $28, 4
            add $14, $14, $12  # dataset1[0]
            add $14, $14, $11  # & dataset1[j]

            lw $15, 0($14)  # dataset[j]
            lw $25, 4($14)  # dataset[j+1]

            slt $24, $15, $25  # check if need to swap
            bne $24, $0, c2notif  # dataset[j] - dataset[j+1] < 0

            # swap
            sw $15, 4($14)
            sw $25, 0($14)

            c2notif:
                # pass

            addi $11, $11, 4  # j++
            j c2forj

        c2ix:
            ori $1, $1, 0  # bypass compile error

        addi $10, $10, 4  # i++
        j c2fori

    c2jx:
        ori $1, $1, 0  # bypass compile error

    skpc2:
        ori $1, $1, 0  # bypass compile error


    # press enter to exit
    c2_loop1:
        lw $3, 0xC80($28)
        beq $3, $0, c2_loop1
    
    c2_loop2:
        lw $3, 0xC80($28)
        bne $3, $0, c2_loop2

    j main


case3:
    # disp case 3
    ori $1, $0, 3
    sw $1, 0xC62($28)

    lw $12, 0x($28)
    sll $12, $12, 2    # sizeof(dataset)

    addi $24, $28, 4  # start of dataset0
    add $25, $24, $12
    add $25, $25, $12  # start of dataset2

    jal copy_dataset_old_start_from_s24_new_from_s25

    lw $12, 0x($28)
    sll $12, $12, 2    # sizeof(dataset)

    addi $25, $28, 4
    add $25, $25, $12
    add $25, $25, $12  # start of dataset2

    ori $11, $zero, 0  # i

    sign_ext_for:
        beq $11, $12, fin_c3f

        lw $24, 0x($25)  # dataset2[i]

        andi $14, $24, 0x80  # bit 8, sign = 0, is positive
        beq $14, $zero, no_need_to_conv

        # convert to 2s complement
        lui $3, 0xFFFF

        andi $24, $24, 0x7F  # keep abs val
        xori $24, $24, 0x7F  # 1s complement
        addi $24, $24, 1  # 2s complement
        ori $24, $24, 0xFF80  # 1s leading
        or $24, $24, $3

        no_need_to_conv:
            # pass

        # save back
        sw $24, 0x($25)

        addi $11, $11, 4  # i++
        addi $25, $25, 4

        j sign_ext_for

    fin_c3f:
        or $0, $0, $0  # pass

    # confirm exit
    c3_loop1:
        lw $3, 0xC80($28)
        beq $3, $0, c3_loop1
    
    c3_loop2:
        lw $3, 0xC80($28)
        bne $3, $0, c3_loop2

    j main


case4:
    # disp case 4
    ori $1, $0, 4
    sw $1, 0xC62($28)

    lw $12, 0x($28)
    sll $12, $12, 2    # sizeof(dataset)

    addi $24, $28, 4  # start of ds0
    add $24, $24, $12 # start of ds1
    add $24, $24, $12 # start of ds2
    add $25, $24, $12  # start of ds3, curr

    jal copy_dataset_old_start_from_s24_new_from_s25

    ori $10, $zero, 4  # i
    lw $12, 0x($28)
    sll $12, $12, 2    # sizeof(dataset)

    addi $3, $12, -4  # 4 * (len-1)
    beq $3, $0, skpc4


    c4fori:
        beq $10, $12, c4jx

        ori $11, $zero, 0  # j
        sub $13, $12, $10  # len - i

        c4forj:
            beq $11, $13, c4ix

            addi $14, $28, 4
            add $14, $14, $12  # dataset1[0]
            add $14, $14, $12  # dataset2[0]
            add $14, $14, $12  # dataset3[0]
            add $14, $14, $11  # & dataset3[j]

            lw $15, 0($14)  # dataset[j]
            lw $25, 4($14)  # dataset[j+1]

            slt $24, $15, $25
            bne $24, $0, c4notif  # dataset[j] - dataset[j+1] < 0

            # swap
            sw $15, 4($14)
            sw $25, 0($14)

            c4notif:
                # pass

            addi $11, $11, 4  # j++
            j c4forj

        c4ix:
            ori $1, $1, 0  # bypass compile error

        addi $10, $10, 4  # i++
        j c4fori

    c4jx:
        ori $1, $1, 0  # bypass compile error

    skpc4:
        ori $1, $1, 0  # bypass compile error

    c4_loop1:
        lw $3, 0xC80($28)
        beq $3, $0, c4_loop1
    
    c4_loop2:
        lw $3, 0xC80($28)
        bne $3, $0, c4_loop2

    j main


case5:
    ori $1, $0, 5
    sw $1, 0xC62($28)

    lw $12, 0($28)
    sll $12, $12, 2    # sizeof(dataset)

    addi $24, $28, 4  # start of first dataset0
    add $24, $24, $12

    lw $14, 0($24) # min 
    add $24, $24, $12 # start of ds2
    addi $24, $24, -4  # end of ds1
    lw $15, 0($24) # max
    sub $15, $15, $14 # calculate result store in $15 register

    sw $15, 0xC60($28)

    c5_loop1:
        lw $24, 0xC80($28)
        beq $24, $0, c5_loop1
    
    c5_loop2:
        lw $24, 0xC80($28)
        bne $24, $0, c5_loop2

    j main

case6:
    ori $1, $0, 6
    sw $1, 0xC62($28)
    
    lw $12, 0x($28)
    sll $12, $12, 2    # sizeof(dataset)
    addi $24, $28, 4  # start of ds0
    add $24, $24, $12  # start of ds1
    add $24, $24, $12  # start of ds2
    add $24, $24, $12  # start of ds3

    lw $14, 0x($24) # min
    add $24, $24, $12  # ds4 ng
    addi $24, $24, -4  # end of ds3
    lw $15, 0x($24) # max
    sub $15, $15, $14 # calculate result store in $15 register

    sw $15, 0xC60($28)

    c6_loop1:
        lw $24, 0xC80($28)
        beq $24, $0, c6_loop1
    
    c6_loop2:
        lw $24, 0xC80($28)
        bne $24, $0, c6_loop2

    j main

case7:
    ori $1, $0, 7  
    sw $1, 0xC62($28)

    addi $29, $28, 165  # after 41 ints, no one uses this
    addi $29, $29, 4
    jal read  # place number of data in a dataset to sp[0]

    addi $29, $29, 4
    jal read  # place number of data in a dataset to sp[0]

    addi $29, $29, -4
    lw $14, 0($29) # selection of dataset
    addi $14, $14, 1

    lw $12, 0($28)
    sll $12, $12, 2    # sizeof(dataset)
    addi $24, $28, 4  # start of first dataset

    addi $1, $0, 1
    dataset_selection:
        add $24, $24, $12  #FIX1
        addi $14, $14, -1
        bne $14, $0, dataset_selection

    sub $24, $24, $12 # start of the selected dataset

    addi $29, $29, 4
    lw $14, 0($29) # selection of element
    addi $14, $14, 1

    element_selection:
        addi $24, $24, 4
        addi $14, $14, -1
        bne $14, $0, element_selection

    addi $24, $24, -4
    lw $15, 0x($24) # value of the selected element

    andi $15, $15, 0x00FF # mask

    sw $15, 0xC60($28) # display result

    c7_loop1:
        lw $24, 0xC80($28)
        beq $24, $0, c7_loop1
    
    c7_loop2:
        lw $24, 0xC80($28)
        bne $24, $0, c7_loop2

    j main

case8:
    ori $1, $0, 8
    sw $1, 0xC62($28)

    addi $29, $28, 165
    addi $29, $29, 4  # bias+32, good enough

    jal read  # place number of data in a dataset to sp[0]

    lw $14, 0x($29) # selection of element
    sll $14, $14, 2

    lw $12, 0x($28)
    sll $12, $12, 2    # sizeof(dataset)
    addi $24, $28, 4  # start of first dataset


    add $24, $24, $14

    lw $11, 0x($24) # value of the selected element in dataset0

    lw $16, 0x($29)  # idx


    addi $24, $28, 4  # start of ds0
    add $24, $24, $12 # start of ds1
    add $24, $24, $12 # start of ds2

    lw $14, 0x($29) # selection of element
    sll $14, $14, 2

    add $24, $24, $14

    lw $12, 0x($24) # value of the selected element in dataset2
    andi $12, $12, 0xFF

    add $17, $0, $16  # idx
    ori $17, $17, 0x80    # ds2, 10_ _ idx

    ori $15, $15, 0x6DA # 5s
    sll $15, $15, 16
    ori $15, $15, 0xC2C0 # 5s

    srl $15, $15, 5  # scale rate

    ori $8, $0, 0 # cycle counter

    addi $1, $0, 1
    c8disp:
        beq $8, $0, dispds0
            # disp ds1 + its info
            sw $12, 0xC60($28)
            sw $17, 0xC62($28)

        j aftdisp
        dispds0:
            # disp ds0 + its info
            sw $11, 0xC60($28)
            sw $16, 0xC62($28)

        aftdisp:
            sub $8, $1, $8  # disp next ds in next cycle

        ori $2, $0, 0

        wait5s:
            addi $2, $2, 1
            or $0, $0, $0
            beq $2, $15, c8disp
            j wait5s

    j main
