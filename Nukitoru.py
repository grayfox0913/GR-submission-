# coding: utf-8
from glob import glob
import re
import MeCab


def MecabC(text):
    tagger = MeCab.Tagger('-Ochasen')
    tagger.parse('')
    node = tagger.parseToNode(text)
    keywords = []
    while node:
        if node.feature.split(",")[0] == u"名詞":#0、名詞　or 1, 固有名詞
             keywords.append(node.surface)
        node = node.next
    return keywords



m = MeCab.Tagger()
m.parse('')
sen2 = [] #名詞または固有名詞が入る
senp = [] #名刺が入ってる行番号


for file in glob("/Users/kudokosei/Downloads/make-meidai-dialogue-master/nuccのコピー2/"+ '/*.txt'):
    f=open(file,"r",encoding='UTF-8')
    data=f.readlines()
    #print(len(data))

    c = 0
    a = ""
    while(c<(len(data)-4)):
        for i in data[c]:
            if len(MecabC(data[c]))>0:
                sen2.append(MecabC(data[c]))
                senp.append(c)

            c+=1
    
    print(sen2)
    print(senp)
    f.close()

##dataを形態素解析した場合aに2つ以上入っている場合がある