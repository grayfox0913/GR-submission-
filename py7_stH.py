import random
def win_lose_print(ha,hae,wl):
    print('相手は' + janken[hae] + 'です。')
    print('あなたは'+ janken[ha] + 'です。')
    if (wl == 0):
        print('あなたの勝ちです。おめでとう！')
    elif wl == 1:
        print("あいこです。")
    else:
        print('あなたの負けです。')
            


janken=["",'グー','チョキ','パー']
w=0
l=0
d=0
f=""

print('これからじゃんけんを始めます。このじゃんけん勝負は、あなたが5回勝てばあなたの勝利。相手が3回勝てば相手の勝利となります。')
while True:
    global wl
    wl = 0
    ha = int(input('グー:1 チョキ:2 パー:3のいずれかを数値で入力してください:'))
    hae = random.randint(1,3)
    if (ha == 1 and hae == 2) or (ha == 2 and hae == 3) or (ha == 3 and hae == 1):
        wl = 0
        w += 1
    elif ha == hae:
        wl = 1
        d += 1
    else:
        wl = 2
        l += 1
    win_lose_print(ha,hae,wl)
    
    f = (input('終了しますか？(Y/N):'))
    if f == "y":
        break
        
with open("text5.txt","w",encoding="utf_8") as te:
    te.write("勝った回数は" + str(w) + "回です。\n負けた回数は" + str(l) + "回です。\nあいこの回数は" + str(d) + "回です。")

