/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * AbstractApplicationContext 委派执行 post-processor 的工具类
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		// 无论如何，优先执行 BeanDefinitionRegistryPostProcessors

		// 将已经处理过的 BeanFactoryPostProcessor 放在 processedBeans 中，Set集合防止重复执行
		Set<String> processedBeans = new HashSet<>();

		// 判断传入的 beanFactory 是否是 BeanDefinitionRegistry 类型
		// 这里入参的 beanFactory 是在 prepareBeanFactory() 中创建的 DefaultListableBeanFactory
		// DefaultListableBeanFactory 实现了 BeanDefinitionRegistry 接口，所以返回true
		if (beanFactory instanceof BeanDefinitionRegistry) {
			// 将 beanFactory 转换为 BeanDefinitionRegistry
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

			/**
			 * 此处注意区分 BeanFactoryPostProcessor 和 BeanDefinitionRegistryPostProcessor 。
			 * BeanDefinitionRegistryPostProcessor 是 BeanFactoryPostProcessor 的子接口
			 * BeanDefinitionRegistryPostProcessor 主要针对的对象是 BeanDefinition
			 * BeanFactoryPostProcessor 主要针对的对象是 BeanFactory
			 */
			// 该集合用于存放实现了 BeanFactoryPostProcessor 的 post-processor
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			// 该集合用于存放实现了 BeanDefinitionRegistryPostProcessor 的 post-processor
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();


			/**
			 * 该循环首先处理入参中 beanFactoryPostProcessor
			 * 循环遍历，将 BeanDefinitionRegistryPostProcessor 和 BeanFactoryPostProcessor 分开
			 * 分别放入 registryProcessors 和 regularPostProcessors 列表中
			 */
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				// 如果实现了 BeanDefinitionRegistryPostProcessor
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					// 先直接执行 BeanDefinitionRegistryPostProcessor 中的 postProcessBeanDefinitionRegistry() 方法
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					// 再将该postProcessor放入存放实现了 BeanDefinitionPostProcessor 的registryProcessors集合中
					// 用于后续执行其父接口中的 postProcessBeanFactory() 方法
					registryProcessors.add(registryProcessor);
				}
				else {
					// 否则就是普通的 BeanFactoryPostProcessor ，放入用于存放实现了BeanFactoryPostProcessor 的 regularPostProcessors 集合中。
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			/**
			 * 不要在此处初始化 FactoryBeans：我们需要保留所有的bean未初始化以让 beanFactory post-processor (后置增强器)应用于这些bean.
			 * 此外还需要将实现了 PriorityOrdered,Ordered 顺序接口的 BeanDefinitionRegistryPostProcessor 分开处理
			 */

			// 上面说了需要分开不同顺序的postProcessor，所以该列表用于存放当前正在处理的 BeanDefinitionRegistryPostProcessor
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			/**
			 * 首先，执行实现了 PriorityOrdered 的 BeanDefinitionRegistryPostProcessor
			 */

			// 查找所有实现了 BeanDefinitionRegistryPostProcessor 接口的beanName
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			// 遍历处理所有符合规则的postProcessorNames
			for (String ppName : postProcessorNames) {
				// 如果实现了 PriorityOrdered 接口
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					// 获取beanName对应的bean实例，添加到currentRegistryProcessors中
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					// 将要被执行的 BeanDefinitionRegistryPostProcessor 的名称添加到processedBeans，避免后续重复执行
					processedBeans.add(ppName);
				}
			}
			// 按照优先级进行排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 添加到存放实现了 BeanDefinitionRegistryPostProcessor 接口的 registryProcessors 中，用于后续执行 postProcessorBeanFactory() 方法
			registryProcessors.addAll(currentRegistryProcessors);
			// 遍历 currentRegistryProcessors ，执行 postProcessBeanDefinitionRegistry() 方法
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 执行完毕后，清空当前正在处理的 BeanDefinitionRegistryPostProcessor 列表 currentRegistryProcessors
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			/**
			 * 然后，执行实现了 Ordered 的 BeanDefinitionRegistryPostProcessor
			 */

			// 查找所有实现了 BeanDefinitionRegistryPostProcessor 接口的beanName
			// 此处需要重复查找的原因在于上面的执行过程中可能会新增其他的 BeanDefinitionRegistryPostProcessor
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			// 遍历处理所有符合规则的postProcessorNames
			for (String ppName : postProcessorNames) {
				// 如果实现了 PriorityOrdered 接口
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					// 获取beanName对应的bean实例，添加到currentRegistryProcessors中
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					// 将要被执行的 BeanDefinitionRegistryPostProcessor 的名称添加到processedBeans，避免后续重复执行
					processedBeans.add(ppName);
				}
			}
			// 按照优先级进行排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 添加到存放实现了 BeanDefinitionRegistryPostProcessor 接口的 registryProcessors 中，用于后续执行 postProcessorBeanFactory() 方法
			registryProcessors.addAll(currentRegistryProcessors);
			// 遍历 currentRegistryProcessors ，执行 postProcessBeanDefinitionRegistry() 方法
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 执行完毕后，清空当前正在处理的 BeanDefinitionRegistryPostProcessor 列表 currentRegistryProcessors
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			/**
			 * 最后， 执行剩余的未实现任何顺序接口的所有 BeanDefinitionRegistryPostProcessor
			 */
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				// 再次找出所有实现了 BeanDefinitionRegistryPostProcessor 接口的beanName
				// 此处再次重复查找 BeanDefinitionRegistryPostProcessor，且在循环中执行，目的就是保证所有的BeanDefinitionRegistryPostProcessor都能被执行到
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				// 遍历所有符合规则的 beanName
				for (String ppName : postProcessorNames) {
					// 根据 processedBeans 集合的记录，跳过已经执行的 BeanDefinitionRegistryPostProcessor
					if (!processedBeans.contains(ppName)) {
						// 根据name或者对应的实例，并添加到currentRegistryProcessors中
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						// 将要被执行的 BeanDefinitionRegistryPostProcessor 的名称添加到processedBeans，避免后续重复执行
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				// 按照优先级排序 beanDefinitionRegistryPostProcessor
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				// 添加到存放实现了 BeanDefinitionRegistryPostProcessor 接口的 registryProcessors 中，用于后续执行 postProcessorBeanFactory() 方法
				registryProcessors.addAll(currentRegistryProcessors);
				// 遍历 currentRegistryProcessors ，执行 postProcessBeanDefinitionRegistry() 方法
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				// 执行完毕后，清空当前正在处理的 BeanDefinitionRegistryPostProcessor 列表 currentRegistryProcessors
				currentRegistryProcessors.clear();
			}

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			/**
			 * 现在，可以执行 BeanFactoryPostProcessor 中的 postProcessorBeanFactory() 方法
			 */
			// 执行实现了接口 BeanDefinitionRegistryPostProcessor 的 postProcessorBeanFactory() 方法
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			// 执行调用入参 beanFactoryPostProcessors 中的普通 BeanFactoryPostProcessor 的 postProcessBeanFactory() 方法
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}

		else {
			// Invoke factory processors registered with the context instance.
			// 如果beanFactory不归属于BeanDefinitionRegistry类型，那么直接执行postProcessBeanFactory方法
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		/**
		 * 到这里为止，入参beanFactoryPostProcessors和容器中的所有BeanDefinitionRegistryPostProcessor已经全部处理完毕
		 * 下面开始处理容器中所有的BeanFactoryPostProcessor
		 * 可能会包含一些实现类，只实现了BeanFactoryPostProcessor，并没有实现BeanDefinitionRegistryPostProcessor接口
		 * 因此下面开始查找并处理实现了 BeanFactoryPostProcessor 接口的实现类
		 */

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// 找到所有实现 BeanFactoryPostProcessor 接口的类
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 用于存放实现了PriorityOrdered接口的BeanFactoryPostProcessor
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 用于存放实现了Ordered接口的BeanFactoryPostProcessor的beanName
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 用于存放普通BeanFactoryPostProcessor的beanName
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();

		// 遍历postProcessorNames,将BeanFactoryPostProcessor按实现PriorityOrdered、实现Ordered接口、普通三种区分开
		for (String ppName : postProcessorNames) {
			// 跳过已经执行过的BeanFactoryPostProcessor
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			// 添加实现了PriorityOrdered接口的BeanFactoryPostProcessor到priorityOrderedPostProcessors
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			// 添加实现了Ordered接口的BeanFactoryPostProcessor的beanName到orderedPostProcessorNames
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			// 添加剩下的普通BeanFactoryPostProcessor的beanName到nonOrderedPostProcessorNames
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		// 首先，对实现了PriorityOrdered接口的BeanFactoryPostProcessor进行排序
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		// 遍历实现了PriorityOrdered接口的BeanFactoryPostProcessor，执行postProcessBeanFactory方法
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		// 然后，创建存放实现了Ordered接口的BeanFactoryPostProcessor集合
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		// 遍历存放实现了Ordered接口的BeanFactoryPostProcessor名字的集合
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		// 对实现了Ordered接口的BeanFactoryPostProcessor进行排序操作
		sortPostProcessors(orderedPostProcessors, beanFactory);
		// 遍历实现了Ordered接口的BeanFactoryPostProcessor，执行postProcessBeanFactory方法
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		// 最后，创建存放普通的BeanFactoryPostProcessor的集合
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		// 遍历存放实现了普通BeanFactoryPostProcessor名字的集合
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			// 将普通的BeanFactoryPostProcessor添加到集合中
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		// 遍历普通的BeanFactoryPostProcessor，执行postProcessBeanFactory方法
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		// 清除元数据缓存（mergeBeanDefinitions、allBeanNamesByType、singletonBeanNameByType）
		// 因为后置处理器可能已经修改了原始元数据，例如，替换值中的占位符
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// Finally, re-register all internal BeanPostProcessors.
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		// Nothing to sort?
		if (postProcessors.size() <= 1) {
			return;
		}
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
