/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.build.release

import org.gradle.api.internal.project.DefaultProject
import org.gradle.util.HelperUtil

/**
 * @author Hans Dockter
 */
class VersionTest extends GroovyTestCase {
    Version version
    boolean trunk
    DefaultProject project
    Svn svn

    void setUp() {
        File testDir = HelperUtil.makeNewTestDir()
        File rootDir = new File(testDir, 'root')
        rootDir.mkdir()
        project = HelperUtil.createRootProject(rootDir)
        project.previousMajor = '1'
        project.previousMinor = '2'
        project.previousRevision = '3'
        svn = [isTrunk: {trunk}] as Svn
        version = new Version(svn, project, false)
    }

    void tearDown() {
        HelperUtil.deleteTestDir()
    }

    void testFalseTrunkMinor() {
        trunk = true
        assertEquals(1, version.major)
        assertEquals(3, version.minor)
        assertEquals(0, version.revision)
    }

    void testTrueTrunkMinor() {
        trunk = false
        assertEquals(1, version.major)
        assertEquals(2, version.minor)
        assertEquals(4, version.revision)
    }

    void testFalseTrunkMajor() {
        version = new Version(svn, project, true)
        trunk = true
        assertEquals(2, version.major)
        assertEquals(0, version.minor)
        assertEquals(0, version.revision)
    }

    void testTrueTrunkMajor() {
        version = new Version(svn, project, true)
        trunk = false
        assertEquals(1, version.major)
        assertEquals(2, version.minor)
        assertEquals(4, version.revision)
    }

    void testToString() {
        trunk = false
        project.versionModifier = ''
        assertEquals("1.2.4", version.toString())
        project.versionModifier = 'a'
        assertEquals("1.2.4-a", version.toString())

        trunk = true
        project.versionModifier = ''
        assertEquals("1.3", version.toString())
        project.versionModifier = 'a'
        assertEquals("1.3-a", version.toString())
    }

    void testStoreCurrentVersion() {
        trunk = false
        version.storeCurrentVersion()
        Properties properties = new Properties()
        properties.load(new FileInputStream(new File(project.projectDir, 'gradle.properties')))
        assertEquals('1', properties.previousMajor)
        assertEquals('2', properties.previousMinor)
        assertEquals('4', properties.previousRevision)
    }
}
