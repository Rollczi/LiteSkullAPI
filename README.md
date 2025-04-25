# 💜 LiteSkullAPI
#### Fast and extensive skull library for Bukkit/Spigot/Paper plugins.
Helpful links:
- [Support Discord](https://discord.gg/6cUhkj6uZJ)
- [GitHub issues](https://github.com/Rollczi/LiteSkullAPI/issues)

### mineCodes Repository (Maven or Gradle)  ❤️
```xml
<repository>
  <id>minecodes-repository</id>
  <url>https://repository.minecodes.pl/releases</url>
</repository>
```
```groovy
maven { url "https://repository.minecodes.pl/releases" }
```
### Dependencies (Maven or Gradle)
Framework Core
```xml
<dependency>
    <groupId>dev.rollczi</groupId>
    <artifactId>liteskullapi</artifactId>
    <version>2.0.0</version>
</dependency>
```
```groovy
implementation("dev.rollczi:liteskullapi:2.0.0")
```
### How use LiteSkullAPI?
```java
public final class ExamplePlugin extends JavaPlugin {
    private SkullAPI skullAPI;

    @Override
    public void onEnable() {
        this.skullAPI = LiteSkullFactory.builder()
                .cacheExpireAfterWrite(Duration.ofMinutes(45L))
                .bukkitScheduler(this)
                .build();
    }
}
```
#### Accept synchronous with Minecraft Server
Lambda will be run in the server sync task (see `.bukkitScheduler()` or `.scheduler()`)
```java
// you can use this item when skull will be found (synchronous)
skullAPI.acceptSyncSkull("Rollczi", itemStack -> {
    player.getInventory().addItem(itemStack);
});

skullAPI.acceptSyncSkullData("Rollczi", skullData -> {
    String value = skullData.getValue();
    String signature = skullData.getSignature();
});
```
#### Accept asynchronous
```java
// you can use this item when skull will be found (asynchronous)
skullAPI.acceptAsyncSkull("Rollczi", itemStack -> {
    itemStack
    // some code
});
skullAPI.acceptAsyncSkullData("Rollczi", skullData -> {
    String value = skullData.getValue(); // texture value
    String signature = skullData.getSignature(); // texture signature
});
```
#### CompletableFuture
```java
// you can get completable future with skull item
CompletableFuture<ItemStack> completableFuture = skullAPI.getSkull("Rollczi");
completableFuture.thenAcceptAsync(itemStack -> {
    itemStack
    // some code
});

// you can get completable future with skull data
CompletableFuture<SkullData> completableFuture = skullAPI.getSkullData("Rollczi");
completableFuture.thenAcceptAsync(skullData -> {
    String value = skullData.getValue(); // texture value
    String signature = skullData.getSignature(); // texture signature
});
```
#### Await for skull (⚠️ Blocking)
```java
ItemStack itemStack = skullAPI.awaitForSkull("Rollczi", 10, TimeUnit.SECONDS);
SkullData skullData = skullAPI.awaitForSkullData("Rollczi", 10, TimeUnit.SECONDS);
```
