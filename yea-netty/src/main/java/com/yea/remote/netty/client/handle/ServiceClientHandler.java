/**
 * Copyright 2017 伊永飞
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yea.remote.netty.client.handle;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.yea.core.base.act.AbstractAct;
import com.yea.core.exception.YeaException;
import com.yea.core.remote.constants.RemoteConstants;
import com.yea.core.remote.struct.Message;
import com.yea.remote.netty.balancing.RemoteClient;
import com.yea.remote.netty.balancing.RemoteClientLocator;
import com.yea.remote.netty.constants.NettyConstants;
import com.yea.remote.netty.handle.AbstractServiceHandler;
import com.yea.remote.netty.handle.NettyChannelHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * 客户端接到服务端响应后的处理：
 * 1、如果需回调Act，则新起线程执行回调
 * 2、通知Promise已收到响应
 * @author yiyongfei
 * 
 */
public class ServiceClientHandler extends AbstractServiceHandler implements NettyChannelHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceClientHandler.class);
    private ForkJoinPool pool = new ForkJoinPool();
    
    @Override
	protected void execute(ChannelHandlerContext ctx, Message message) throws Exception {
		// TODO Auto-generated method stub
		if (message.getHeader().getType() == RemoteConstants.MessageType.SERVICE_RESP.value()) {
			long times = new Date().getTime() - ((Date)message.getHeader().getAttachment().get(NettyConstants.REQUEST_DATE)).getTime();
            LOGGER.info(ctx.channel().localAddress() + "从" + ctx.channel().remoteAddress() + "接收到响应" + ", 共用时：" + (times) + "，其中：请求用时：" + (((Date)message.getHeader().getAttachment().get(NettyConstants.REQUEST_RECIEVE_DATE)).getTime() - ((Date)message.getHeader().getAttachment().get(NettyConstants.REQUEST_DATE)).getTime()) + "，处理用时：" + (((Date)message.getHeader().getAttachment().get(NettyConstants.HEADER_DATE)).getTime() - ((Date)message.getHeader().getAttachment().get(NettyConstants.REQUEST_RECIEVE_DATE)).getTime()) + "，响应用时：" + (new Date().getTime() - ((Date)message.getHeader().getAttachment().get(NettyConstants.HEADER_DATE)).getTime()));
			if (times > RemoteClient.SLOW_LIMIT) {
				//处理速度达到慢的限制，降低慢权重
				RemoteClient client = RemoteClientLocator.getRemoteClient(ctx.channel());
				if (client != null) {
					client.slowdown();
				}
			}
            
            if(message.getHeader().getAttachment().get(NettyConstants.CALL_ACT) != null) {
				pool.execute(new InnerTask(this.getApplicationContext(), message));
            }
            
            notifyObservers(message.getHeader().getSessionID(), message.getHeader(), message.getBody());
        } else {
            ctx.fireChannelRead(message);
        }
	}
    
    public ChannelHandler clone() throws CloneNotSupportedException {
        ServiceClientHandler obj = null;
        obj = (ServiceClientHandler) super.clone();
        return obj;
    }

    class InnerTask extends RecursiveAction {
    	private static final long serialVersionUID = 1L;
    	private ApplicationContext springContext;
    	private Message message;

    	public InnerTask(ApplicationContext springContext, Message message) {
    		this.springContext = springContext;
    		this.message = message;
    	}

		@Override
		protected void compute() {
			// TODO Auto-generated method stub
			AbstractAct<?> act = (AbstractAct<?>) springContext.getBean((String) message.getHeader().getAttachment().get(NettyConstants.CALL_ACT));
			AbstractAct<?> cloneAct = null;
			try {
				cloneAct = act.clone();
				cloneAct.setApplicationContext(springContext);
				if (RemoteConstants.MessageResult.SUCCESS.value() == message.getHeader().getResult()) {
					cloneAct.setMessages((Object[]) message.getBody());
				} else {
					cloneAct.setThrowable((Exception) message.getBody());
				}
				cloneAct.fork();
			} catch (CloneNotSupportedException ex) {
				throw new YeaException(ex);
			} finally {
				cloneAct = null;
			}
		}
    }
}
