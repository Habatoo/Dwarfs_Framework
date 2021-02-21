/*
 * Copyright 2021 the original author or authors.
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

package com.angrydwarfs.framework.repository;

import com.angrydwarfs.framework.models.Activity;
import com.angrydwarfs.framework.models.Tag;
import com.angrydwarfs.framework.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findById(Long id);
    Optional<Activity> findByActivityIndex(String activityIndex);
    List<Activity> findByUserActivities(User user);
    List<Activity> findByTags(Tag tag);

}
