package com.by.bledemo.DataProcess;

import android.app.Application;
import android.app.ListActivity;
import android.content.Context;
import android.gesture.Gesture;
import android.speech.tts.Voice;
import android.widget.Toast;

import com.by.bledemo.Gesture.BasicGesture;
import com.by.bledemo.R;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by zxcv1 on 2018/4/19.
 */

public class RecognitionWorker {

    private String Upward = "Upward";
    private String Downward = "Downward";
    private String Inward = "Inward";
    private String Outward = "Outward";
    private String Raise = "Raise";
    private String DontCare = "DontCare";

    public VoiceData VoiceData = new VoiceData();

    private int LanquageSelector = 0;
    private int[] zero = {R.raw.zero, R.raw.zeroen};
    private int[] one = {R.raw.one, R.raw.oneen};
    private int[] two = {R.raw.two, R.raw.twoen};
    private int[] three = {R.raw.three, R.raw.threeen};
    private int[] four = {R.raw.four, R.raw.fouren};
    private int[] five = {R.raw.five, R.raw.fiveen};
    private int[] six = {R.raw.six, R.raw.sixen};
    private int[] seven = {R.raw.seven, R.raw.sevenen};
    private int[] eight = {R.raw.eight, R.raw.eighten};
    private int[] nine = {R.raw.nine, R.raw.nineen};
    private int[] ten = {R.raw.ten, R.raw.tenen};
    private int[] twenty = {R.raw.twenty, R.raw.twentyen};
    private int[] thirty = {R.raw.thirty, R.raw.thirtyen};
    private int[] forty = {R.raw.forty, R.raw.fortyen};
    private int[] fifty = {R.raw.fifty, R.raw.fiftyen};
    private int[] sixty = {R.raw.sixty, R.raw.sixtyen};
    private int[] seventy = {R.raw.seventy, R.raw.seventyen};
    private int[] eighty = {R.raw.eighty, R.raw.eightyen};
    private int[] ninety = {R.raw.ninety, R.raw.ninetyen};
    private int[] hundred = {R.raw.hundred, R.raw.hundreden};
    private int[] thousand = {R.raw.thousand, R.raw.thousanden};
    private int[] male = {R.raw.male, R.raw.maleen};
    private int[] female = {R.raw.female, R.raw.femaleen};
    private int[] brother = {R.raw.brother, R.raw.brotheren};
    private int[] sister = {R.raw.sister, R.raw.sisteren};
    private int[] money = {R.raw.pay, R.raw.payen};
    private int[] toilet = {R.raw.toilet, R.raw.toileten};
    private int[] thanks = {R.raw.thanks,R.raw.thanksen};
    private int[] taipei = {R.raw.taipei,R.raw.taipeien};
    private int[] technology = {R.raw.technology,R.raw.technologyen};
    private int[] university = {R.raw.university,R.raw.universityen};
    private int[] you = {R.raw.you, R.raw.youen};
    private int[] hello = {R.raw.hello, R.raw.helloen};
    private int[] love = {R.raw.love,R.raw.loveen};
    private int[] protect = {R.raw.protect,R.raw.protecten};
    private int[] coffee = {R.raw.coffee,R.raw.coffeeen};
    private int[] admit = {R.raw.admin,R.raw.adminen};
    private int[] help = {R.raw.help,R.raw.helpen};
    private int[] lonely = {R.raw.lonely,R.raw.lonelyen};
    private int[] I = {R.raw.i,R.raw.ien};
    private int[] letter = {R.raw.letter,R.raw.letteren};
    private int[] recletter = {R.raw.recletter,R.raw.recletteren};
    private int[] stamp = {R.raw.stamp,R.raw.stampen};
    private int[] sandwich = {R.raw.sandwich,R.raw.sandwichen};
    private int[] welcome = {R.raw.welcome,R.raw.welcomeen};
    private int[] graduation = {R.raw.graduation,R.raw.graduationen};
    private int[] teacher = {R.raw.teacher,R.raw.teacheren};

    public RecognitionWorker(String State){

        if(State.equals("Chinese")){
            LanquageSelector = 0;
        }else if(State.equals("English")){
            LanquageSelector = 1;
        }
        VoiceData.Null = 0;
        VoiceData.zero = zero[LanquageSelector];
        VoiceData.one = one[LanquageSelector];
        VoiceData.two = two[LanquageSelector];
        VoiceData.three = three[LanquageSelector];
        VoiceData.four = four[LanquageSelector];
        VoiceData.five = five[LanquageSelector];
        VoiceData.six = six[LanquageSelector];
        VoiceData.seven = seven[LanquageSelector];
        VoiceData.eight = eight[LanquageSelector];
        VoiceData.nine = nine[LanquageSelector];
        VoiceData.ten = ten[LanquageSelector];
        VoiceData.twenty = twenty[LanquageSelector];
        VoiceData.thirty = thirty[LanquageSelector];
        VoiceData.forty = forty[LanquageSelector];
        VoiceData.fifty = fifty[LanquageSelector];
        VoiceData.sixty = sixty[LanquageSelector];
        VoiceData.seventy = seventy[LanquageSelector];
        VoiceData.eighty = eighty[LanquageSelector];
        VoiceData.ninety = ninety[LanquageSelector];
        VoiceData.hundred = hundred[LanquageSelector];
        VoiceData.thousand = thousand[LanquageSelector];
        VoiceData.male = male[LanquageSelector];
        VoiceData.female = female[LanquageSelector];
        VoiceData.brother = brother[LanquageSelector];
        VoiceData.sister = sister[LanquageSelector];
        VoiceData.money = money[LanquageSelector];
        VoiceData.toilet = toilet[LanquageSelector];
        VoiceData.thanks = thanks[LanquageSelector];
        VoiceData.taipei = taipei[LanquageSelector];
        VoiceData.technology = technology[LanquageSelector];
        VoiceData.university = university[LanquageSelector];
        VoiceData.you = you[LanquageSelector];
        VoiceData.hello = hello[LanquageSelector];
        VoiceData.love = love[LanquageSelector];
        VoiceData.protect = protect[LanquageSelector];
        VoiceData.coffee = coffee[LanquageSelector];
        VoiceData.admit = admit[LanquageSelector];
        VoiceData.help = help[LanquageSelector];
        VoiceData.lonely = lonely[LanquageSelector];
        VoiceData.I = I[LanquageSelector];
        VoiceData.letter = letter[LanquageSelector];
        VoiceData.recletter = recletter[LanquageSelector];
        VoiceData.stamp = stamp[LanquageSelector];
        VoiceData.sandwich = sandwich[LanquageSelector];
        VoiceData.welcome = welcome[LanquageSelector];
        VoiceData.graduation = graduation[LanquageSelector];
        VoiceData.teacher = teacher[LanquageSelector];
    }
    //靜態手勢33
    public ArrayList<HandRecognition> handRecognitions = new ArrayList<HandRecognition>();
    public void StaticVocabulary(){
        //參考
        //handRecognitions.add(new HandRecognition("中文", "英文", mp3id, 左手面相, 右手面向, "左手手勢", "右手手勢"));

        //左手
        handRecognitions.add(new HandRecognition("零", "zero", VoiceData.zero, Raise, DontCare, BasicGesture.zero, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("一", "one", VoiceData.one, Raise, DontCare, BasicGesture.one, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("二", "two", VoiceData.two, Raise, DontCare, BasicGesture.two, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("三", "three", VoiceData.three, Raise, DontCare, BasicGesture.three, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("四", "four", VoiceData.four, Raise, DontCare, BasicGesture.four, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("五", "five", VoiceData.five, Raise, DontCare, BasicGesture.five, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("六", "six", VoiceData.six, Inward, DontCare, BasicGesture.six, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("七", "seven", VoiceData.seven, Inward, DontCare, BasicGesture.seven, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("八", "eight", VoiceData.eight, Inward, DontCare, BasicGesture.eight, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("九", "nine", VoiceData.nine, Inward, DontCare, BasicGesture.nine, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("十", "ten", VoiceData.ten, Inward, DontCare, BasicGesture.ten, BasicGesture.DontCare));
        /*handRecognitions.add(new HandRecognition("二十", "twenty", VoiceData.twenty, Raise, DontCare, BasicGesture.twenty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("三十", "thirty", VoiceData.thirty, Raise, DontCare, BasicGesture.thirty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("四十", "forty", VoiceData.forty, Raise, DontCare, BasicGesture.forty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("五十", "fifty", VoiceData.fifty, Raise, DontCare, BasicGesture.fifty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("六十", "sixty", VoiceData.sixty, Raise, DontCare, BasicGesture.sixty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("七十", "seventy", VoiceData.seventy, Raise, DontCare, BasicGesture.seventy, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("八十", "eighty", VoiceData.eighty, Raise, DontCare, BasicGesture.eighty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("九十", "ninety", VoiceData.ninety, Raise, DontCare, BasicGesture.ninety, BasicGesture.DontCare));*/
        handRecognitions.add(new HandRecognition("百", "hundred", VoiceData.hundred, Raise, DontCare, BasicGesture.hundred, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("千", "thousand", VoiceData.thousand, Raise, DontCare, BasicGesture.thousand, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("男", "male", VoiceData.male, Inward, DontCare, BasicGesture.male, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("女", "female", VoiceData.female, Raise, DontCare, BasicGesture.female, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("哥哥", "brother", VoiceData.brother, Raise, DontCare, BasicGesture.brother, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("姊姊", "sister", VoiceData.sister, Raise, DontCare, BasicGesture.sister, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("繳錢", "pay", VoiceData.money, Inward, DontCare, BasicGesture.money, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("廁所", "toilet", VoiceData.toilet, Raise, DontCare, BasicGesture.WC, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("你", "you", VoiceData.you, Inward, DontCare, BasicGesture.one, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("_你好", "hello", R.raw.nulll, Raise, DontCare, BasicGesture.fist, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("我", "I", VoiceData.I, Downward, DontCare, BasicGesture.six, BasicGesture.DontCare));


        //右手
        /*handRecognitions.add(new HandRecognition("零", "zero", VoiceData.zero, DontCare, Raise, BasicGesture.DontCare, BasicGesture.zero));
        handRecognitions.add(new HandRecognition("一", "one", VoiceData.one, DontCare, Raise, BasicGesture.DontCare, BasicGesture.one));
        handRecognitions.add(new HandRecognition("二", "two", VoiceData.two, DontCare, Raise, BasicGesture.DontCare, BasicGesture.two));
        handRecognitions.add(new HandRecognition("三", "three", VoiceData.three, DontCare, Raise, BasicGesture.DontCare, BasicGesture.three));
        handRecognitions.add(new HandRecognition("四", "four", VoiceData.four, DontCare, Raise, BasicGesture.DontCare, BasicGesture.four));
        handRecognitions.add(new HandRecognition("五", "five", VoiceData.five, DontCare, Raise, BasicGesture.DontCare, BasicGesture.five_A));
        handRecognitions.add(new HandRecognition("五", "five", VoiceData.five, DontCare, Raise, BasicGesture.DontCare, BasicGesture.five_B));
        handRecognitions.add(new HandRecognition("六", "six", VoiceData.six, DontCare, Inward, BasicGesture.DontCare, BasicGesture.six));
        handRecognitions.add(new HandRecognition("七", "seven", VoiceData.seven, DontCare, Inward, BasicGesture.DontCare, BasicGesture.seven));
        handRecognitions.add(new HandRecognition("八", "eight", VoiceData.eight, DontCare, Inward, BasicGesture.DontCare, BasicGesture.eight));
        handRecognitions.add(new HandRecognition("九", "nine", VoiceData.nine, DontCare, Inward, BasicGesture.DontCare, BasicGesture.nine));
        handRecognitions.add(new HandRecognition("十", "ten", VoiceData.ten, DontCare, Raise, BasicGesture.DontCare, BasicGesture.ten_N));
        handRecognitions.add(new HandRecognition("十", "ten", VoiceData.ten, DontCare, Raise, BasicGesture.DontCare, BasicGesture.ten_S));*/
        handRecognitions.add(new HandRecognition("孤單", "lonely", VoiceData.lonely, DontCare, Inward, BasicGesture.DontCare, BasicGesture.male));

        //雙手
        handRecognitions.add(new HandRecognition("謝謝", "thanks", VoiceData.thanks, Inward, Inward, BasicGesture.male, BasicGesture.male));
        handRecognitions.add(new HandRecognition("臺北", "taipei", VoiceData.taipei, Raise, Raise, BasicGesture.six, BasicGesture.six));
        handRecognitions.add(new HandRecognition("科", "tech", R.raw.nulll, Inward, Downward, BasicGesture.seven, BasicGesture.fist));
        handRecognitions.add(new HandRecognition("技", "nology", R.raw.nulll, Inward, Downward, BasicGesture.six, BasicGesture.fist));
        handRecognitions.add(new HandRecognition("大學", "university", VoiceData.university, Raise, Raise, BasicGesture.one, BasicGesture.one));
        handRecognitions.add(new HandRecognition("棕", "brown", R.raw.nulll, Inward, Outward, BasicGesture.fist, BasicGesture.brown));
        handRecognitions.add(new HandRecognition("寄信", "Send Letter", VoiceData.letter, Inward, Upward, BasicGesture.two, BasicGesture.two));
        handRecognitions.add(new HandRecognition("收信", "Receive Letter", VoiceData.recletter, Inward, Inward, BasicGesture.two, BasicGesture.two));
        handRecognitions.add(new HandRecognition("郵", "stamp1", R.raw.nulll, Downward, Inward, BasicGesture.two, BasicGesture.two));
        handRecognitions.add(new HandRecognition("票", "stamp2", R.raw.nulll, Downward, Downward, BasicGesture.two, BasicGesture.two));
        handRecognitions.add(new HandRecognition("畢業", "graduation", VoiceData.graduation, Raise, Raise, BasicGesture.fist, BasicGesture.fist));
    }

    //動態手勢9
    public ArrayList<MotionRecognition> motionRecognitions = new ArrayList<MotionRecognition>();
    public void MotionVocabulary(){
        //參考
        //motionRecognitions.add(new MotionRecognition("中文", "英文", mp3id, "左手面相", "右手面向", "左手手勢", "右手手勢", LAx~z, RAx~z, LAF0~LAF4, RAF0~RAF4));
        motionRecognitions.add(new MotionRecognition("愛","love",VoiceData.love,Downward,Inward,BasicGesture.hand,BasicGesture.male,
                -15,-10000,-10000,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("保護","protect",VoiceData.protect,Inward,Inward,BasicGesture.hand,BasicGesture.male,
                -10000,-10000,-15,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("錄取","admit",VoiceData.admit,Upward,Inward,BasicGesture.hand,BasicGesture.male,
                -10000,-10000,-60,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("幫忙","help",VoiceData.help,Raise,Inward,BasicGesture.hand,BasicGesture.male,
                -10000,-15,-10000,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        /*motionRecognitions.add(new MotionRecognition("孤單","lonely",VoiceData.lonely,DontCare,Inward,BasicGesture.hand,BasicGesture.male,
                -15,-10000,-10000,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));*/
        motionRecognitions.add(new MotionRecognition("攪拌","stir",R.raw.nulll,Inward,Downward,BasicGesture.fist,BasicGesture.two,
                -10000,-10000,-10000,
                -10000,-10000,-100,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("三明治","sandwich",VoiceData.sandwich,Inward,Inward,BasicGesture.three,BasicGesture.hand,
                -10000,-10000,-25,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("歡迎","welcome",VoiceData.welcome,Upward,Upward,BasicGesture.hand,BasicGesture.hand,
                -15,-10000,-10000,
                -15,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("老師","teacher",VoiceData.teacher,Downward,DontCare,BasicGesture.four,BasicGesture.DontCare,
                -10000,-10000,-25,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));

        //另外6個移動特徵
        motionRecognitions.add(new MotionRecognition("U-like movement","U-like movement",VoiceData.Null,Downward,Downward,BasicGesture.one,BasicGesture.hand,
                -20,-10000,-10000,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("L-like movement","L-like movement",VoiceData.Null,Downward,Downward,BasicGesture.two,BasicGesture.hand,
                -10000,-20,-10000,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("J-like movement","J-like movement",VoiceData.Null,Downward,Downward,BasicGesture.three,BasicGesture.hand,
                -10000,-20,-10000,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("arm waving","arm waving",VoiceData.Null,Downward,Downward,BasicGesture.four,BasicGesture.hand,
                -10000,-60,-10000,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("wrist waving","wrist waving",VoiceData.Null,Downward,Downward,BasicGesture.hand,BasicGesture.hand,
                -10000,-15,-10000,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));
        motionRecognitions.add(new MotionRecognition("wrist rotation","wrist rotation",VoiceData.Null,Upward,Downward,BasicGesture.hand,BasicGesture.hand,
                -10000,-15,-10000,
                -10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000,
                -10000,-10000,-10000,-10000,-10000));

    }

    //組合手勢4
    public ArrayList<CombinationWordRecognition> combinationWordRecognitions = new ArrayList<CombinationWordRecognition>();
    public void CombinationVocabulary(){
        //參考
        //combinationWordRecognitions.add(new CombinationWordRecognition("中文", "英文", mp3D, "第一階段", "第二階段"));
        combinationWordRecognitions.add(new CombinationWordRecognition("科技", "Technology", VoiceData.technology, "科", "技", "tech","nology"));
        combinationWordRecognitions.add(new CombinationWordRecognition("你好", "Hello", VoiceData.hello, "你", "_你好", "you", "hello"));
        combinationWordRecognitions.add(new CombinationWordRecognition("咖啡", "Coffee", VoiceData.coffee, "棕", "攪拌", "brown", "stir"));
        combinationWordRecognitions.add(new CombinationWordRecognition("郵票", "Stamp", VoiceData.stamp, "郵", "票", "stamp1", "stamp2"));
    }

}
