#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class NSString, NSSet<ObjectType>, NSObject, NSNumber, NSMutableSet<ObjectType>, NSMutableDictionary<KeyType, ObjectType>, NSMutableArray<ObjectType>, NSError, NSDictionary<KeyType, ObjectType>, NSArray<ObjectType>, Allshared__SkieTypeExportsKt, Allshared__SkieSuspendWrappersKt, AllsharedUShort, AllsharedULong, AllsharedUInt, AllsharedUByte, AllsharedStartSDKKt, AllsharedSkie_SuspendResultSuccess, AllsharedSkie_SuspendResultError, AllsharedSkie_SuspendResultCanceled, AllsharedSkie_SuspendResult, AllsharedSkie_SuspendHandler, AllsharedSkie_CancellationHandler, AllsharedSkieKotlinStateFlow<T>, AllsharedSkieKotlinSharedFlow<T>, AllsharedSkieKotlinOptionalStateFlow<T>, AllsharedSkieKotlinOptionalSharedFlow<T>, AllsharedSkieKotlinOptionalMutableStateFlow<T>, AllsharedSkieKotlinOptionalMutableSharedFlow<T>, AllsharedSkieKotlinOptionalFlow<T>, AllsharedSkieKotlinMutableStateFlow<T>, AllsharedSkieKotlinMutableSharedFlow<T>, AllsharedSkieKotlinFlow<T>, AllsharedSkieColdFlowIterator<E>, AllsharedShort, AllsharedSDKHandle, AllsharedNumber, AllsharedMutableSet<ObjectType>, AllsharedMutableDictionary<KeyType, ObjectType>, AllsharedLong, AllsharedKotlinThrowable, AllsharedKotlinRuntimeException, AllsharedKotlinIllegalStateException, AllsharedKotlinException, AllsharedKotlinEnumCompanion, AllsharedKotlinEnum<E>, AllsharedKotlinCancellationException, AllsharedKotlinArray<T>, AllsharedInt, AllsharedHttpClientAnalytics, AllsharedFloat, AllsharedDouble, AllsharedByte, AllsharedBreedsBreedRepositoryCompanion, AllsharedBreedsBreedRepository, AllsharedBreedsBreedDataStateEmpty, AllsharedBreedsBreedDataStateCached, AllsharedBreedsBreedDataState, AllsharedBreedsBreedDataEventRefreshedSuccess, AllsharedBreedsBreedDataEventLoading, AllsharedBreedsBreedDataEventInitial, AllsharedBreedsBreedDataEventError, AllsharedBreedsBreedDataEvent, AllsharedBreedsBreed, AllsharedBreedAnalyticsNotFetchedReason, AllsharedBreedAnalytics, AllsharedBoolean, AllsharedBase, AllsharedAppAnalytics, AllsharedAnalyticsKt, AllsharedAnalyticsHandle;

@protocol NSCopying, AllsharedSkie_DispatcherDelegate, AllsharedKotlinx_coroutines_coreStateFlow, AllsharedKotlinx_coroutines_coreSharedFlow, AllsharedKotlinx_coroutines_coreRunnable, AllsharedKotlinx_coroutines_coreMutableStateFlow, AllsharedKotlinx_coroutines_coreMutableSharedFlow, AllsharedKotlinx_coroutines_coreFlowCollector, AllsharedKotlinx_coroutines_coreFlow, AllsharedKotlinIterator, AllsharedKotlinComparable, AllsharedBreedsBreedDataRefreshState, AllsharedAnalytics;

// Due to an Obj-C/Swift interop limitation, SKIE cannot generate Swift types with a lambda type argument.
// Example of such type is: A<() -> Unit> where A<T> is a generic class.
// To avoid compilation errors SKIE replaces these type arguments with __SkieLambdaErrorType, resulting in A<__SkieLambdaErrorType>.
// Generated declarations that reference __SkieLambdaErrorType cannot be called in any way and the __SkieLambdaErrorType class cannot be used.
// The original declarations can still be used in the same way as other declarations hidden by SKIE (and with the same limitations as without SKIE).
@interface __SkieLambdaErrorType : NSObject
- (instancetype _Nonnull)init __attribute__((unavailable));
+ (instancetype _Nonnull)new __attribute__((unavailable));
@end

// Due to an Obj-C/Swift interop limitation, SKIE cannot generate Swift code that uses external Obj-C types for which SKIE doesn't know a fully qualified name.
// This problem occurs when custom Cinterop bindings are used because those do not contain the name of the Framework that provides implementation for those binding.
// The name can be configured manually using the SKIE Gradle configuration key 'ClassInterop.CInteropFrameworkName' in the same way as other SKIE features.
// To avoid compilation errors SKIE replaces types with unknown Framework name with __SkieUnknownCInteropFrameworkErrorType.
// Generated declarations that reference __SkieUnknownCInteropFrameworkErrorType cannot be called in any way and the __SkieUnknownCInteropFrameworkErrorType class cannot be used.
@interface __SkieUnknownCInteropFrameworkErrorType : NSObject
- (instancetype _Nonnull)init __attribute__((unavailable));
+ (instancetype _Nonnull)new __attribute__((unavailable));
@end


NS_ASSUME_NONNULL_BEGIN
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-warning-option"
#pragma clang diagnostic ignored "-Wincompatible-property-type"
#pragma clang diagnostic ignored "-Wnullability"

#pragma push_macro("_Nullable_result")
#if !__has_feature(nullability_nullable_result)
#undef _Nullable_result
#define _Nullable_result _Nullable
#endif

__attribute__((swift_name("KotlinBase")))
@interface AllsharedBase : NSObject
- (instancetype)init __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
+ (void)initialize __attribute__((objc_requires_super));
@end

@interface AllsharedBase (AllsharedBaseCopying) <NSCopying>
@end

__attribute__((swift_name("KotlinMutableSet")))
@interface AllsharedMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end

__attribute__((swift_name("KotlinMutableDictionary")))
@interface AllsharedMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end

@interface NSError (NSErrorAllsharedKotlinException)
@property (readonly) id _Nullable kotlinException;
@end

__attribute__((swift_name("KotlinNumber")))
@interface AllsharedNumber : NSNumber
- (instancetype)initWithChar:(char)value __attribute__((unavailable));
- (instancetype)initWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
- (instancetype)initWithShort:(short)value __attribute__((unavailable));
- (instancetype)initWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
- (instancetype)initWithInt:(int)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
- (instancetype)initWithLong:(long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
- (instancetype)initWithLongLong:(long long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
- (instancetype)initWithFloat:(float)value __attribute__((unavailable));
- (instancetype)initWithDouble:(double)value __attribute__((unavailable));
- (instancetype)initWithBool:(BOOL)value __attribute__((unavailable));
- (instancetype)initWithInteger:(NSInteger)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
+ (instancetype)numberWithChar:(char)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
+ (instancetype)numberWithShort:(short)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
+ (instancetype)numberWithInt:(int)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
+ (instancetype)numberWithLong:(long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
+ (instancetype)numberWithLongLong:(long long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
+ (instancetype)numberWithFloat:(float)value __attribute__((unavailable));
+ (instancetype)numberWithDouble:(double)value __attribute__((unavailable));
+ (instancetype)numberWithBool:(BOOL)value __attribute__((unavailable));
+ (instancetype)numberWithInteger:(NSInteger)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
@end

__attribute__((swift_name("KotlinByte")))
@interface AllsharedByte : AllsharedNumber
- (instancetype)initWithChar:(char)value;
+ (instancetype)numberWithChar:(char)value;
@end

__attribute__((swift_name("KotlinUByte")))
@interface AllsharedUByte : AllsharedNumber
- (instancetype)initWithUnsignedChar:(unsigned char)value;
+ (instancetype)numberWithUnsignedChar:(unsigned char)value;
@end

__attribute__((swift_name("KotlinShort")))
@interface AllsharedShort : AllsharedNumber
- (instancetype)initWithShort:(short)value;
+ (instancetype)numberWithShort:(short)value;
@end

__attribute__((swift_name("KotlinUShort")))
@interface AllsharedUShort : AllsharedNumber
- (instancetype)initWithUnsignedShort:(unsigned short)value;
+ (instancetype)numberWithUnsignedShort:(unsigned short)value;
@end

__attribute__((swift_name("KotlinInt")))
@interface AllsharedInt : AllsharedNumber
- (instancetype)initWithInt:(int)value;
+ (instancetype)numberWithInt:(int)value;
@end

__attribute__((swift_name("KotlinUInt")))
@interface AllsharedUInt : AllsharedNumber
- (instancetype)initWithUnsignedInt:(unsigned int)value;
+ (instancetype)numberWithUnsignedInt:(unsigned int)value;
@end

__attribute__((swift_name("KotlinLong")))
@interface AllsharedLong : AllsharedNumber
- (instancetype)initWithLongLong:(long long)value;
+ (instancetype)numberWithLongLong:(long long)value;
@end

__attribute__((swift_name("KotlinULong")))
@interface AllsharedULong : AllsharedNumber
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value;
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value;
@end

__attribute__((swift_name("KotlinFloat")))
@interface AllsharedFloat : AllsharedNumber
- (instancetype)initWithFloat:(float)value;
+ (instancetype)numberWithFloat:(float)value;
@end

__attribute__((swift_name("KotlinDouble")))
@interface AllsharedDouble : AllsharedNumber
- (instancetype)initWithDouble:(double)value;
+ (instancetype)numberWithDouble:(double)value;
@end

__attribute__((swift_name("KotlinBoolean")))
@interface AllsharedBoolean : AllsharedNumber
- (instancetype)initWithBool:(BOOL)value;
+ (instancetype)numberWithBool:(BOOL)value;
@end

__attribute__((swift_name("Analytics")))
@protocol AllsharedAnalytics
@required
- (void)sendEventEventName:(NSString *)eventName eventArgs:(NSDictionary<NSString *, id> *)eventArgs __attribute__((swift_name("sendEvent(eventName:eventArgs:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("AnalyticsHandle")))
@interface AllsharedAnalyticsHandle : AllsharedBase
@property (readonly) AllsharedAppAnalytics *appAnalytics __attribute__((swift_name("appAnalytics")));
@property (readonly) AllsharedBreedAnalytics *breedAnalytics __attribute__((swift_name("breedAnalytics")));
@property (readonly) AllsharedHttpClientAnalytics *httpClientAnalytics __attribute__((swift_name("httpClientAnalytics")));
- (instancetype)initWithAppAnalytics:(AllsharedAppAnalytics *)appAnalytics breedAnalytics:(AllsharedBreedAnalytics *)breedAnalytics httpClientAnalytics:(AllsharedHttpClientAnalytics *)httpClientAnalytics __attribute__((swift_name("init(appAnalytics:breedAnalytics:httpClientAnalytics:)"))) __attribute__((objc_designated_initializer));
- (AllsharedAnalyticsHandle *)doCopyAppAnalytics:(AllsharedAppAnalytics *)appAnalytics breedAnalytics:(AllsharedBreedAnalytics *)breedAnalytics httpClientAnalytics:(AllsharedHttpClientAnalytics *)httpClientAnalytics __attribute__((swift_name("doCopy(appAnalytics:breedAnalytics:httpClientAnalytics:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("AppAnalytics")))
@interface AllsharedAppAnalytics : AllsharedBase
- (void)appStarted __attribute__((swift_name("appStarted()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedAnalytics")))
@interface AllsharedBreedAnalytics : AllsharedBase
- (void)breedsFetchedFromNetworkSize:(int32_t)size __attribute__((swift_name("breedsFetchedFromNetwork(size:)")));
- (void)breedsNotFetchedFromNetworkReason:(AllsharedBreedAnalyticsNotFetchedReason *)reason __attribute__((swift_name("breedsNotFetchedFromNetwork(reason:)")));
- (void)clearingBreedViewModel __attribute__((swift_name("clearingBreedViewModel()")));
- (void)databaseCleared __attribute__((swift_name("databaseCleared()")));
- (void)displayingBreedsSize:(int32_t)size __attribute__((swift_name("displayingBreeds(size:)")));
- (void)displayingErrorMessage:(AllsharedBreedAnalyticsNotFetchedReason *)message __attribute__((swift_name("displayingError(message:)")));
- (void)favoriteClickedId:(int64_t)id __attribute__((swift_name("favoriteClicked(id:)")));
- (void)favoriteSavedId:(int64_t)id favorite:(BOOL)favorite __attribute__((swift_name("favoriteSaved(id:favorite:)")));
- (void)fetchingBreedsFromNetwork __attribute__((swift_name("fetchingBreedsFromNetwork()")));
- (void)insertingBreedsToDatabaseSize:(int32_t)size __attribute__((swift_name("insertingBreedsToDatabase(size:)")));
- (void)refreshingBreeds __attribute__((swift_name("refreshingBreeds()")));
- (void)updatingBreedsErrorThrowable:(AllsharedKotlinThrowable *)throwable __attribute__((swift_name("updatingBreedsError(throwable:)")));
@end

__attribute__((swift_name("KotlinComparable")))
@protocol AllsharedKotlinComparable
@required
- (int32_t)compareToOther:(id _Nullable)other __attribute__((swift_name("compareTo(other:)")));
@end

__attribute__((swift_name("KotlinEnum")))
@interface AllsharedKotlinEnum<E> : AllsharedBase <AllsharedKotlinComparable>
@property (class, readonly, getter=companion) AllsharedKotlinEnumCompanion *companion __attribute__((swift_name("companion")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) int32_t ordinal __attribute__((swift_name("ordinal")));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer));
- (int32_t)compareToOther:(E)other __attribute__((swift_name("compareTo(other:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedAnalytics.NotFetchedReason")))
@interface AllsharedBreedAnalyticsNotFetchedReason : AllsharedKotlinEnum<AllsharedBreedAnalyticsNotFetchedReason *>
@property (class, readonly) AllsharedBreedAnalyticsNotFetchedReason *notstale __attribute__((swift_name("notstale")));
@property (class, readonly) AllsharedBreedAnalyticsNotFetchedReason *networkerror __attribute__((swift_name("networkerror")));
@property (class, readonly) AllsharedBreedAnalyticsNotFetchedReason *randomfail __attribute__((swift_name("randomfail")));
@property (class, readonly) NSArray<AllsharedBreedAnalyticsNotFetchedReason *> *entries __attribute__((swift_name("entries")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (AllsharedKotlinArray<AllsharedBreedAnalyticsNotFetchedReason *> *)values __attribute__((swift_name("values()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("HttpClientAnalytics")))
@interface AllsharedHttpClientAnalytics : AllsharedBase
- (void)logMessageMessage:(NSString *)message __attribute__((swift_name("logMessage(message:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SDKHandle")))
@interface AllsharedSDKHandle : AllsharedBase
@property (readonly) AllsharedAppAnalytics *appAnalytics __attribute__((swift_name("appAnalytics")));
@property (readonly) AllsharedBreedAnalytics *breedAnalytics __attribute__((swift_name("breedAnalytics")));
@property (readonly) AllsharedBreedsBreedRepository *breedRepository __attribute__((swift_name("breedRepository")));
- (instancetype)initWithBreedRepository:(AllsharedBreedsBreedRepository *)breedRepository appAnalytics:(AllsharedAppAnalytics *)appAnalytics breedAnalytics:(AllsharedBreedAnalytics *)breedAnalytics __attribute__((swift_name("init(breedRepository:appAnalytics:breedAnalytics:)"))) __attribute__((objc_designated_initializer));
- (AllsharedSDKHandle *)doCopyBreedRepository:(AllsharedBreedsBreedRepository *)breedRepository appAnalytics:(AllsharedAppAnalytics *)appAnalytics breedAnalytics:(AllsharedBreedAnalytics *)breedAnalytics __attribute__((swift_name("doCopy(breedRepository:appAnalytics:breedAnalytics:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieColdFlowIterator")))
@interface AllsharedSkieColdFlowIterator<E> : AllsharedBase
- (instancetype)initWithFlow:(id<AllsharedKotlinx_coroutines_coreFlow>)flow __attribute__((swift_name("init(flow:)"))) __attribute__((objc_designated_initializer));
- (void)cancel __attribute__((swift_name("cancel()")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)hasNextWithCompletionHandler:(void (^)(AllsharedBoolean * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("hasNext(completionHandler:)")));
- (E _Nullable)next __attribute__((swift_name("next()")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlow")))
@protocol AllsharedKotlinx_coroutines_coreFlow
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinFlow")))
@interface AllsharedSkieKotlinFlow<__covariant T> : AllsharedBase <AllsharedKotlinx_coroutines_coreFlow>
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreSharedFlow")))
@protocol AllsharedKotlinx_coroutines_coreSharedFlow <AllsharedKotlinx_coroutines_coreFlow>
@required
@property (readonly) NSArray<id> *replayCache __attribute__((swift_name("replayCache")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlowCollector")))
@protocol AllsharedKotlinx_coroutines_coreFlowCollector
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)emitValue:(id _Nullable)value completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("emit(value:completionHandler:)")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreMutableSharedFlow")))
@protocol AllsharedKotlinx_coroutines_coreMutableSharedFlow <AllsharedKotlinx_coroutines_coreSharedFlow, AllsharedKotlinx_coroutines_coreFlowCollector>
@required

/**
 * @note annotations
 *   kotlinx.coroutines.ExperimentalCoroutinesApi
*/
- (void)resetReplayCache __attribute__((swift_name("resetReplayCache()")));
- (BOOL)tryEmitValue:(id _Nullable)value __attribute__((swift_name("tryEmit(value:)")));
@property (readonly) id<AllsharedKotlinx_coroutines_coreStateFlow> subscriptionCount __attribute__((swift_name("subscriptionCount")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinMutableSharedFlow")))
@interface AllsharedSkieKotlinMutableSharedFlow<T> : AllsharedBase <AllsharedKotlinx_coroutines_coreMutableSharedFlow>
@property (readonly) NSArray<T> *replayCache __attribute__((swift_name("replayCache")));
@property (readonly) id<AllsharedKotlinx_coroutines_coreStateFlow> subscriptionCount __attribute__((swift_name("subscriptionCount")));
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreMutableSharedFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)emitValue:(T)value completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("emit(value:completionHandler:)")));

/**
 * @note annotations
 *   kotlinx.coroutines.ExperimentalCoroutinesApi
*/
- (void)resetReplayCache __attribute__((swift_name("resetReplayCache()")));
- (BOOL)tryEmitValue:(T)value __attribute__((swift_name("tryEmit(value:)")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreStateFlow")))
@protocol AllsharedKotlinx_coroutines_coreStateFlow <AllsharedKotlinx_coroutines_coreSharedFlow>
@required
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreMutableStateFlow")))
@protocol AllsharedKotlinx_coroutines_coreMutableStateFlow <AllsharedKotlinx_coroutines_coreStateFlow, AllsharedKotlinx_coroutines_coreMutableSharedFlow>
@required
- (void)setValue:(id _Nullable)value __attribute__((swift_name("setValue(_:)")));
- (BOOL)compareAndSetExpect:(id _Nullable)expect update:(id _Nullable)update __attribute__((swift_name("compareAndSet(expect:update:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinMutableStateFlow")))
@interface AllsharedSkieKotlinMutableStateFlow<T> : AllsharedBase <AllsharedKotlinx_coroutines_coreMutableStateFlow>
@property (readonly) NSArray<T> *replayCache __attribute__((swift_name("replayCache")));
@property (readonly) id<AllsharedKotlinx_coroutines_coreStateFlow> subscriptionCount __attribute__((swift_name("subscriptionCount")));
@property T value __attribute__((swift_name("value")));
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreMutableStateFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
- (BOOL)compareAndSetExpect:(T)expect update:(T)update __attribute__((swift_name("compareAndSet(expect:update:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)emitValue:(T)value completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("emit(value:completionHandler:)")));

/**
 * @note annotations
 *   kotlinx.coroutines.ExperimentalCoroutinesApi
*/
- (void)resetReplayCache __attribute__((swift_name("resetReplayCache()")));
- (BOOL)tryEmitValue:(T)value __attribute__((swift_name("tryEmit(value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinOptionalFlow")))
@interface AllsharedSkieKotlinOptionalFlow<__covariant T> : AllsharedBase <AllsharedKotlinx_coroutines_coreFlow>
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinOptionalMutableSharedFlow")))
@interface AllsharedSkieKotlinOptionalMutableSharedFlow<T> : AllsharedBase <AllsharedKotlinx_coroutines_coreMutableSharedFlow>
@property (readonly) NSArray<id> *replayCache __attribute__((swift_name("replayCache")));
@property (readonly) id<AllsharedKotlinx_coroutines_coreStateFlow> subscriptionCount __attribute__((swift_name("subscriptionCount")));
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreMutableSharedFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)emitValue:(T _Nullable)value completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("emit(value:completionHandler:)")));

/**
 * @note annotations
 *   kotlinx.coroutines.ExperimentalCoroutinesApi
*/
- (void)resetReplayCache __attribute__((swift_name("resetReplayCache()")));
- (BOOL)tryEmitValue:(T _Nullable)value __attribute__((swift_name("tryEmit(value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinOptionalMutableStateFlow")))
@interface AllsharedSkieKotlinOptionalMutableStateFlow<T> : AllsharedBase <AllsharedKotlinx_coroutines_coreMutableStateFlow>
@property (readonly) NSArray<id> *replayCache __attribute__((swift_name("replayCache")));
@property (readonly) id<AllsharedKotlinx_coroutines_coreStateFlow> subscriptionCount __attribute__((swift_name("subscriptionCount")));
@property T _Nullable value __attribute__((swift_name("value")));
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreMutableStateFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
- (BOOL)compareAndSetExpect:(T _Nullable)expect update:(T _Nullable)update __attribute__((swift_name("compareAndSet(expect:update:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)emitValue:(T _Nullable)value completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("emit(value:completionHandler:)")));

/**
 * @note annotations
 *   kotlinx.coroutines.ExperimentalCoroutinesApi
*/
- (void)resetReplayCache __attribute__((swift_name("resetReplayCache()")));
- (BOOL)tryEmitValue:(T _Nullable)value __attribute__((swift_name("tryEmit(value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinOptionalSharedFlow")))
@interface AllsharedSkieKotlinOptionalSharedFlow<__covariant T> : AllsharedBase <AllsharedKotlinx_coroutines_coreSharedFlow>
@property (readonly) NSArray<id> *replayCache __attribute__((swift_name("replayCache")));
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreSharedFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinOptionalStateFlow")))
@interface AllsharedSkieKotlinOptionalStateFlow<__covariant T> : AllsharedBase <AllsharedKotlinx_coroutines_coreStateFlow>
@property (readonly) NSArray<id> *replayCache __attribute__((swift_name("replayCache")));
@property (readonly) T _Nullable value __attribute__((swift_name("value")));
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreStateFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinSharedFlow")))
@interface AllsharedSkieKotlinSharedFlow<__covariant T> : AllsharedBase <AllsharedKotlinx_coroutines_coreSharedFlow>
@property (readonly) NSArray<T> *replayCache __attribute__((swift_name("replayCache")));
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreSharedFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SkieKotlinStateFlow")))
@interface AllsharedSkieKotlinStateFlow<__covariant T> : AllsharedBase <AllsharedKotlinx_coroutines_coreStateFlow>
@property (readonly) NSArray<T> *replayCache __attribute__((swift_name("replayCache")));
@property (readonly) T value __attribute__((swift_name("value")));
- (instancetype)initWithDelegate:(id<AllsharedKotlinx_coroutines_coreStateFlow>)delegate __attribute__((swift_name("init(_:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Skie_CancellationHandler")))
@interface AllsharedSkie_CancellationHandler : AllsharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)cancel __attribute__((swift_name("cancel()")));
@end

__attribute__((swift_name("Skie_DispatcherDelegate")))
@protocol AllsharedSkie_DispatcherDelegate
@required
- (void)dispatchBlock:(id<AllsharedKotlinx_coroutines_coreRunnable>)block __attribute__((swift_name("dispatch(block:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Skie_SuspendHandler")))
@interface AllsharedSkie_SuspendHandler : AllsharedBase
- (instancetype)initWithCancellationHandler:(AllsharedSkie_CancellationHandler *)cancellationHandler dispatcherDelegate:(id<AllsharedSkie_DispatcherDelegate>)dispatcherDelegate onResult:(void (^)(AllsharedSkie_SuspendResult *))onResult __attribute__((swift_name("init(cancellationHandler:dispatcherDelegate:onResult:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("Skie_SuspendResult")))
@interface AllsharedSkie_SuspendResult : AllsharedBase
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Skie_SuspendResult.Canceled")))
@interface AllsharedSkie_SuspendResultCanceled : AllsharedSkie_SuspendResult
@property (class, readonly, getter=shared) AllsharedSkie_SuspendResultCanceled *shared __attribute__((swift_name("shared")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)canceled __attribute__((swift_name("init()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Skie_SuspendResult.Error")))
@interface AllsharedSkie_SuspendResultError : AllsharedSkie_SuspendResult
@property (readonly) NSError *error __attribute__((swift_name("error")));
- (instancetype)initWithError:(NSError *)error __attribute__((swift_name("init(error:)"))) __attribute__((objc_designated_initializer));
- (AllsharedSkie_SuspendResultError *)doCopyError:(NSError *)error __attribute__((swift_name("doCopy(error:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Skie_SuspendResult.Success")))
@interface AllsharedSkie_SuspendResultSuccess : AllsharedSkie_SuspendResult
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
- (instancetype)initWithValue:(id _Nullable)value __attribute__((swift_name("init(value:)"))) __attribute__((objc_designated_initializer));
- (AllsharedSkie_SuspendResultSuccess *)doCopyValue:(id _Nullable)value __attribute__((swift_name("doCopy(value:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("AnalyticsKt")))
@interface AllsharedAnalyticsKt : AllsharedBase
+ (AllsharedAnalyticsHandle *)doInitAnalyticsAnalytics:(id<AllsharedAnalytics>)analytics __attribute__((swift_name("doInitAnalytics(analytics:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("StartSDKKt")))
@interface AllsharedStartSDKKt : AllsharedBase
+ (NSString *)sayHello __attribute__((swift_name("sayHello()")));
+ (AllsharedSDKHandle *)startSDKAnalytics:(id<AllsharedAnalytics>)analytics __attribute__((swift_name("startSDK(analytics:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("__SkieSuspendWrappersKt")))
@interface Allshared__SkieSuspendWrappersKt : AllsharedBase
+ (void)Skie_Suspend__0__hasNextDispatchReceiver:(AllsharedSkieColdFlowIterator<id> *)dispatchReceiver suspendHandler:(AllsharedSkie_SuspendHandler *)suspendHandler __attribute__((swift_name("Skie_Suspend__0__hasNext(dispatchReceiver:suspendHandler:)")));
+ (void)Skie_Suspend__1__collectDispatchReceiver:(id<AllsharedKotlinx_coroutines_coreFlow>)dispatchReceiver collector:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)collector suspendHandler:(AllsharedSkie_SuspendHandler *)suspendHandler __attribute__((swift_name("Skie_Suspend__1__collect(dispatchReceiver:collector:suspendHandler:)")));
+ (void)Skie_Suspend__2__emitDispatchReceiver:(id<AllsharedKotlinx_coroutines_coreFlowCollector>)dispatchReceiver value:(id _Nullable)value suspendHandler:(AllsharedSkie_SuspendHandler *)suspendHandler __attribute__((swift_name("Skie_Suspend__2__emit(dispatchReceiver:value:suspendHandler:)")));
+ (void)Skie_Suspend__3__refreshBreedsDispatchReceiver:(AllsharedBreedsBreedRepository *)dispatchReceiver suspendHandler:(AllsharedSkie_SuspendHandler *)suspendHandler __attribute__((swift_name("Skie_Suspend__3__refreshBreeds(dispatchReceiver:suspendHandler:)")));
+ (void)Skie_Suspend__4__refreshBreedsIfStaleDispatchReceiver:(AllsharedBreedsBreedRepository *)dispatchReceiver suspendHandler:(AllsharedSkie_SuspendHandler *)suspendHandler __attribute__((swift_name("Skie_Suspend__4__refreshBreedsIfStale(dispatchReceiver:suspendHandler:)")));
+ (void)Skie_Suspend__5__updateBreedFavoriteDispatchReceiver:(AllsharedBreedsBreedRepository *)dispatchReceiver breed:(AllsharedBreedsBreed *)breed suspendHandler:(AllsharedSkie_SuspendHandler *)suspendHandler __attribute__((swift_name("Skie_Suspend__5__updateBreedFavorite(dispatchReceiver:breed:suspendHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("__SkieTypeExportsKt")))
@interface Allshared__SkieTypeExportsKt : AllsharedBase
+ (void)skieTypeExports_0P0:(AllsharedBreedsBreedDataEvent *)p0 p1:(id<AllsharedBreedsBreedDataRefreshState>)p1 __attribute__((swift_name("skieTypeExports_0(p0:p1:)")));
+ (void)skieTypeExports_1P0:(AllsharedBreedsBreedDataEventError *)p0 p1:(AllsharedBreedsBreedDataEventInitial *)p1 p2:(AllsharedBreedsBreedDataEventLoading *)p2 p3:(AllsharedBreedsBreedDataEventRefreshedSuccess *)p3 p4:(AllsharedBreedsBreedDataStateCached *)p4 p5:(AllsharedBreedsBreedDataStateEmpty *)p5 __attribute__((swift_name("skieTypeExports_1(p0:p1:p2:p3:p4:p5:)")));
@end

__attribute__((swift_name("KotlinThrowable")))
@interface AllsharedKotlinThrowable : AllsharedBase
@property (readonly) AllsharedKotlinThrowable * _Nullable cause __attribute__((swift_name("cause")));
@property (readonly) NSString * _Nullable message __attribute__((swift_name("message")));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));

/**
 * @note annotations
 *   kotlin.experimental.ExperimentalNativeApi
*/
- (AllsharedKotlinArray<NSString *> *)getStackTrace __attribute__((swift_name("getStackTrace()")));
- (void)printStackTrace __attribute__((swift_name("printStackTrace()")));
- (NSString *)description __attribute__((swift_name("description()")));
- (NSError *)asError __attribute__((swift_name("asError()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinEnumCompanion")))
@interface AllsharedKotlinEnumCompanion : AllsharedBase
@property (class, readonly, getter=shared) AllsharedKotlinEnumCompanion *shared __attribute__((swift_name("shared")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinArray")))
@interface AllsharedKotlinArray<T> : AllsharedBase
@property (readonly) int32_t size __attribute__((swift_name("size")));
+ (instancetype)arrayWithSize:(int32_t)size init:(T _Nullable (^)(AllsharedInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (T _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (id<AllsharedKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(T _Nullable)value __attribute__((swift_name("set(index:value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedsBreedRepository")))
@interface AllsharedBreedsBreedRepository : AllsharedBase
@property (class, readonly, getter=companion) AllsharedBreedsBreedRepositoryCompanion *companion __attribute__((swift_name("companion")));
@property (readonly) id<AllsharedKotlinx_coroutines_coreSharedFlow> dataEvents __attribute__((swift_name("dataEvents")));
@property (readonly) id<AllsharedKotlinx_coroutines_coreStateFlow> dataState __attribute__((swift_name("dataState")));
- (id<AllsharedKotlinx_coroutines_coreFlow>)getBreeds __attribute__((swift_name("getBreeds()")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)refreshBreedsWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("refreshBreeds(completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)refreshBreedsIfStaleWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("refreshBreedsIfStale(completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)updateBreedFavoriteBreed:(AllsharedBreedsBreed *)breed completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("updateBreedFavorite(breed:completionHandler:)")));
@end

__attribute__((swift_name("KotlinException")))
@interface AllsharedKotlinException : AllsharedKotlinThrowable
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinRuntimeException")))
@interface AllsharedKotlinRuntimeException : AllsharedKotlinException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinIllegalStateException")))
@interface AllsharedKotlinIllegalStateException : AllsharedKotlinRuntimeException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.4")
*/
__attribute__((swift_name("KotlinCancellationException")))
@interface AllsharedKotlinCancellationException : AllsharedKotlinIllegalStateException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(AllsharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreRunnable")))
@protocol AllsharedKotlinx_coroutines_coreRunnable
@required
- (void)run __attribute__((swift_name("run()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedsBreed")))
@interface AllsharedBreedsBreed : AllsharedBase
@property (readonly) BOOL favorite __attribute__((swift_name("favorite")));
@property (readonly) int64_t id __attribute__((swift_name("id")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
- (instancetype)initWithId:(int64_t)id name:(NSString *)name favorite:(BOOL)favorite __attribute__((swift_name("init(id:name:favorite:)"))) __attribute__((objc_designated_initializer));
- (AllsharedBreedsBreed *)doCopyId:(int64_t)id name:(NSString *)name favorite:(BOOL)favorite __attribute__((swift_name("doCopy(id:name:favorite:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((swift_name("BreedsBreedDataEvent")))
@interface AllsharedBreedsBreedDataEvent : AllsharedBase
@end

__attribute__((swift_name("BreedsBreedDataRefreshState")))
@protocol AllsharedBreedsBreedDataRefreshState
@required
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedsBreedDataEvent.Error")))
@interface AllsharedBreedsBreedDataEventError : AllsharedBreedsBreedDataEvent
@property (readonly) AllsharedBreedAnalyticsNotFetchedReason *reason __attribute__((swift_name("reason")));
- (instancetype)initWithReason:(AllsharedBreedAnalyticsNotFetchedReason *)reason __attribute__((swift_name("init(reason:)"))) __attribute__((objc_designated_initializer));
- (AllsharedBreedsBreedDataEventError *)doCopyReason:(AllsharedBreedAnalyticsNotFetchedReason *)reason __attribute__((swift_name("doCopy(reason:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedsBreedDataEvent.Initial")))
@interface AllsharedBreedsBreedDataEventInitial : AllsharedBreedsBreedDataEvent
@property (class, readonly, getter=shared) AllsharedBreedsBreedDataEventInitial *shared __attribute__((swift_name("shared")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)initial __attribute__((swift_name("init()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedsBreedDataEvent.Loading")))
@interface AllsharedBreedsBreedDataEventLoading : AllsharedBreedsBreedDataEvent <AllsharedBreedsBreedDataRefreshState>
@property (class, readonly, getter=shared) AllsharedBreedsBreedDataEventLoading *shared __attribute__((swift_name("shared")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)loading __attribute__((swift_name("init()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedsBreedDataEvent.RefreshedSuccess")))
@interface AllsharedBreedsBreedDataEventRefreshedSuccess : AllsharedBreedsBreedDataEvent
@property (class, readonly, getter=shared) AllsharedBreedsBreedDataEventRefreshedSuccess *shared __attribute__((swift_name("shared")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)refreshedSuccess __attribute__((swift_name("init()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((swift_name("BreedsBreedDataState")))
@interface AllsharedBreedsBreedDataState : AllsharedBase
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedsBreedDataState.Cached")))
@interface AllsharedBreedsBreedDataStateCached : AllsharedBreedsBreedDataState <AllsharedBreedsBreedDataRefreshState>
@property (readonly) int64_t lastRefresh __attribute__((swift_name("lastRefresh")));
- (instancetype)initWithLastRefresh:(int64_t)lastRefresh __attribute__((swift_name("init(lastRefresh:)"))) __attribute__((objc_designated_initializer));
- (AllsharedBreedsBreedDataStateCached *)doCopyLastRefresh:(int64_t)lastRefresh __attribute__((swift_name("doCopy(lastRefresh:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedsBreedDataState.Empty")))
@interface AllsharedBreedsBreedDataStateEmpty : AllsharedBreedsBreedDataState <AllsharedBreedsBreedDataRefreshState>
@property (class, readonly, getter=shared) AllsharedBreedsBreedDataStateEmpty *shared __attribute__((swift_name("shared")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)empty __attribute__((swift_name("init()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((swift_name("KotlinIterator")))
@protocol AllsharedKotlinIterator
@required
- (BOOL)hasNext __attribute__((swift_name("hasNext()")));
- (id _Nullable)next __attribute__((swift_name("next()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BreedsBreedRepository.Companion")))
@interface AllsharedBreedsBreedRepositoryCompanion : AllsharedBase
@property (class, readonly, getter=shared) AllsharedBreedsBreedRepositoryCompanion *shared __attribute__((swift_name("shared")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@end

#pragma pop_macro("_Nullable_result")
#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
