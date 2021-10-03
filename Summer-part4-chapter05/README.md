# 깃허브 레파지토리 앱

- 깃허브 로그인 기능 구현

- 로그인 중 화면 구현

- 로그인 이후 레포지토리

- 검색 리스트 보여주기

- 검색 레포지토리에 대해서 상세화면

- 찜한 레포지토리 리스트

---

## 순서

1. 코루틴 개념 소개, 비동기적으로 리모트 데이터 불러오기
2. 검색 기능 추가하기
3. 룸 개념 소개
4. 비동기적으로 로컬데이터 쓰기, 읽기

---

## Thread vs Coroutines

### Thread

- Task 단위 = Thread

  - 각 작업에 Thread를 할당한다.
  - 각 Thread는 자체 Stack 메모리를 가지며, JVM Stack 영역을 차지한다.

<br/>

- Context Switching
  - Blocking : Thread1이 Thread2의 결과가 나올때까지 기다려야 한다면 Thread1 은 Blocking 되어 사용하지 못한다.

<br/>

### Coroutines

- Task 단위 = object(Coroutine)

  - 각 작업에 Object(Coroutine)을 할당한다.
  - Coroutine은 객체를 담는 JVM Heap에 적재된다.

<br/>

- Context Switching => No Context Switching

  - 코드를 통해 Switching 시점을 보장
  - Suspend is NonBlocking : Corutine1 이 Corutine2 의 결과가 나올 때까지 기다려야 한다면 Corutine1 은 Suspend 되지만, Corutine1 을 수행하던 Thread 는 유효하다.

  => Coroutine2 도 Coroutine1 과 동일한 Thread 에서 실행할 수 있음.

<br/>

- Suspend(일지 중단 함수)

  - 앞에 suspend 키워드를 붙여서 함수를 구성하는 방법
  - 람다를 구성하여 다른 일시 중단 함수를 호출한다. runBlocking?

<br/>

- Coroutine Dispatcher

  - 코루틴을 시작하거나 재개할 스레드를 결정하기 위한 도구
  - 모든 Dispatcher는 CoroutineDispatcher 인터페이스를 구현해야 한다.

<br/>

- Coroutine Builder
  **- async()**

  - 결과가 전역으로 예상되는 코루틴 시작에 사용 (결과 반환)
  - 전역으로 예외 처리 가능
  - 결과, 예외 반환 가능한 Defered\<T> 반환

  **- launch()**

  - 결과를 반환하지 않는 코루틴 시작에 사용 (결과 반환 X)
  - 자체/자식 코루틴 실행ㅇ르 취소할 수 있는 JOB 반환

  **- runBlocking()**

  - Blocking 코드를 일시 중지 (Suspend) 가능한 코드로 연결하기 위한 용도
  - main 함수나 Unit Test 때 많이 사용됨
  - 코루틴의 실행이 끝날때까지 현재 스레드를 차단함

---

라이브러리 의존성 추가

```gradle
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0'
implementation 'org.jetbrains.kotlinx:kotlinx-corutines-android:1.3.9'
```
