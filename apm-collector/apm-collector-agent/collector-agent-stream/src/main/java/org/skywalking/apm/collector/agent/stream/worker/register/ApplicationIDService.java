/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.collector.agent.stream.worker.register;

import org.skywalking.apm.collector.agent.stream.graph.RegisterStreamGraph;
import org.skywalking.apm.collector.cache.CacheModule;
import org.skywalking.apm.collector.cache.service.ApplicationCacheService;
import org.skywalking.apm.collector.core.graph.Graph;
import org.skywalking.apm.collector.core.graph.GraphManager;
import org.skywalking.apm.collector.core.module.ModuleManager;
import org.skywalking.apm.collector.storage.table.register.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author peng-yongsheng
 */
public class ApplicationIDService {

    private final Logger logger = LoggerFactory.getLogger(ApplicationIDService.class);

    private final ModuleManager moduleManager;
    private final Graph<Application> applicationRegisterGraph;

    public ApplicationIDService(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.applicationRegisterGraph = GraphManager.INSTANCE.createIfAbsent(RegisterStreamGraph.APPLICATION_REGISTER_GRAPH_ID, Application.class);
    }

    public int getOrCreate(String applicationCode) {
        ApplicationCacheService service = moduleManager.find(CacheModule.NAME).getService(ApplicationCacheService.class);
        int applicationId = service.get(applicationCode);

        if (applicationId == 0) {
            Application application = new Application(applicationCode);
            application.setApplicationCode(applicationCode);
            application.setApplicationId(0);

            applicationRegisterGraph.start(application);
        }
        return applicationId;
    }
}
