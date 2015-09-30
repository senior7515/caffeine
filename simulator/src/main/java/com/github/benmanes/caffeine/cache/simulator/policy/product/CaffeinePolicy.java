/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
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
 */
package com.github.benmanes.caffeine.cache.simulator.policy.product;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.simulator.BasicSettings;
import com.github.benmanes.caffeine.cache.simulator.policy.Policy;
import com.github.benmanes.caffeine.cache.simulator.policy.PolicyStats;
import com.typesafe.config.Config;

/**
 * Caffeine cache implementation.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class CaffeinePolicy implements Policy {
  private final Cache<Object, Object> cache;
  private final PolicyStats policyStats;

  public CaffeinePolicy(String name, Config config) {
    policyStats = new PolicyStats(name);
    BasicSettings settings = new BasicSettings(config);
    cache = Caffeine.newBuilder()
        .executor(Runnable::run)
        .maximumSize(settings.maximumSize())
        .initialCapacity(settings.maximumSize())
        .removalListener(notification -> policyStats.recordEviction())
        .build();
  }

  @Override
  public void record(Comparable<Object> key) {
    boolean[] hit = { true };
    cache.get(key, k -> {
      hit[0] = false;
      return k;
    });
    if (hit[0]) {
      policyStats.recordHit();
    } else {
      policyStats.recordMiss();
    }
  }

  @Override
  public PolicyStats stats() {
    return policyStats;
  }
}
