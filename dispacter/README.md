# Dispatcher Shell


## 📝 Hakkında
Bu Java ile yazılmış basit bir kabuktur. Gerçek bir işletim sisteminde süreçlerin nasıl yürütüldüğünü simüle etmek amacıyla yapılan bir projedir.
## 🗺️ Çözüm Yolu

Dağıtıcı kabuğu bir metin dosyasını argüman olarak alır. Metin dosyası yürütülecek işlemlerin bir listesini içerir (aşağıda açıklanmıştır). Kabuk daha sonra dosyayı okur ve dosyadaki her satır için bir işlem oluşturur.
It consists of two major process queues:
1. **Gerçek Zamanlı Kuyruk**: Bu sıra tüm gerçek zamanlı işlemleri içerir (öncelik değeri = 0) ve bunları herhangi bir kesinti olmadan anında çalıştırır. İşlemler önceliklerine ve varış zamanlarına göre sıralanır. Bu kuyruk, işlemleri İlk Gelen İlk Hizmet Algoritmasına (FCFS) göre yürütür.
<br><br>
3. **Kullanıcı İş Kuyruğu**: Bu, tüm kullanıcı işlerini içeren çok düzeyli bir geri bildirim kuyruğudur (öncelik değeri = 1, 2, 3). İşlemler önceliklerine ve varış zamanlarına göre sıralanır. İşlemler daha sonra Round-Robin tarzında yürütülür. Zaman kuantumu 1 saniyedir.
<br><br>

## 🧩 Diyagram
<br>

![A screenshot](./diagram/Blank%20diagram%20(1).png)

<br>

## 🚀 Başlangic
1.  klon repostiry
    <br><br>
    ```
     git clone https://github.com/bedre7/dispatcher-shell.git
    ```
2. Aşağıdaki formatta bir metin dosyasında yürütülecek işlemlerin bir listesini yapın
    <br><br>
   ```
   <arrival time>, <priority[0 - 3]>, <burst time>
   ```
3.Programı terminalinizde çalıştırın
    <br><br>
   ```
   java -jar Program.jar <metin dosyanızın yolu>
   ```