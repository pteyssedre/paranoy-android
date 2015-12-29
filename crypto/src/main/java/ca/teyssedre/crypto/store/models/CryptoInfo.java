/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015. Pierre Teyssedre
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.teyssedre.crypto.store.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.teyssedre.crypto.Crypto;

public class CryptoInfo {

    private Map<String, Set<String>> info;
    private Map<String, Map<String, Set<String>>> tryme;

    public CryptoInfo() {
        info = new HashMap<>();
        tryme = new HashMap<>();
        Set<String> providersNames = Crypto.GetProvidersNames();
        for (String name : providersNames) {
            Set<String> serviceTypes = Crypto.GetServiceTypes(name);
            info.put(name, serviceTypes);
            Map<String, Set<String>> innerMap = new HashMap<>();
            for (String service : serviceTypes) {
                Set<String> algorithms = Crypto.GetAlgorithms(name, service);
                innerMap.put(service, algorithms);
            }
            tryme.put(name, innerMap);
        }
    }

    public Map<String, Set<String>> foundMatch(CryptoInfo cryptoInfo) {
        Map<String, Set<String>> matches = new HashMap<>();
        Set<String> rProviders = cryptoInfo.info.keySet();
        Set<String> mProviders = this.info.keySet();
        for (String provider : mProviders) {
            if (rProviders.contains(provider)) {
                // Provider found
                Set<String> rServiceTypes = cryptoInfo.info.get(provider);
                Set<String> mServiceTypes = this.info.get(provider);
                boolean sF = false;
                Set<String> delta = new HashSet<>();
                for (String serviceType : mServiceTypes) {
                    if (rServiceTypes.contains(serviceType)) {
                        sF = true;
                        delta.add(serviceType);
                    }
                }
                if (sF) {
                    matches.put(provider, delta);
                }
            }
        }
        return matches;
    }

    public Map<String, Map<String, Set<String>>> getTryme() {
        return tryme;
    }
}
