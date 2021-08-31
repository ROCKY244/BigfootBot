package cn.classfun.bigfootbot;
import cn.classfun.bigfootbot.config.Config;
import cn.classfun.bigfootbot.events.*;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.MiraiLogger;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.function.Consumer;
import static cn.classfun.bigfootbot.config.Config.cfg;
public final class BigfootBot {
	public static Bot bot;
	public static MiraiLogger blog;
	public static void main(String[]argv) throws IOException, ReflectiveOperationException{
		String config="/etc/bigfootbot.json";
		final String opt="--config=";
		for(String str:argv)
			if(str.startsWith(opt))
				config=str.substring(opt.length());
		Config.initConfig(config);
		System.out.println("Parse config done");
		System.out.printf("QQ Number: %s\n",cfg.getQQNumber());
		bot=BotFactory.INSTANCE.newBot(
			cfg.getQQNumber(),
			cfg.getQQPassword(),
			new BotConfiguration(){{
				fileBasedDeviceInfo();
				setProtocol(MiraiProtocol.ANDROID_PHONE);
			}}
		);
		blog=bot.getLogger();
		bot.login();
		EventChannel<BotEvent>ev=bot.getEventChannel();
		subscribeAlways(ev,OnBotInvitedJoinGroupRequestEvent.class);
		subscribeAlways(ev,OnGroupMessageEvent.class);
		subscribeAlways(ev,OnGroupMuteAllEvent.class);
		subscribeAlways(ev,OnGroupNameChangeEvent.class);
		subscribeAlways(ev,OnMemberCardChangeEvent.class);
		subscribeAlways(ev,OnMemberJoinEvent.class);
		subscribeAlways(ev,OnMemberJoinRequestEvent.class);
		subscribeAlways(ev,OnMemberMuteEvent.class);
		subscribeAlways(ev,OnMemberUnmuteEvent.class);
		subscribeAlways(ev,OnMemberLeaveEvent.class);
	}
	@SuppressWarnings("unchecked")
	private static<T extends Event>void subscribeAlways(
		EventChannel<BotEvent>ev,
		Class<? extends Consumer<T>>c
	)throws ReflectiveOperationException{
		ParameterizedType x=(ParameterizedType)c.getGenericInterfaces()[0];
		ev.subscribeAlways(
			(Class<? extends T>)x.getActualTypeArguments()[0],
			c.getDeclaredConstructor().newInstance()
		);
	}
}