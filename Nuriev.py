import os
from roboflow import Roboflow
rf = Roboflow(api_key="Eb6wdSGZqotl5ftg4anT")
project = rf.workspace("petri").project("petri-dishes")
model = project.version(1).model

def process_images_from_folder(folder_path):
    # Получаем список всех файлов в указанной папке
    files = os.listdir(folder_path)

    # Фильтруем только изображения (например, файлы с расширением .jpg, .png)
    image_files = [f for f in files if f.endswith(('.jpg', '.jpeg', '.png', '.bmp', '.gif', '.raw'))]

    # Проверяем, есть ли изображения в папке
    if not image_files:
        print("Нет изображений в указанной папке.")
        return

    for image_file in image_files:
        # Полный путь к файлу изображения
        image_path = os.path.join(folder_path, image_file)

        # Выполняем предсказание
        prediction = model.predict(image_path, confidence=50, overlap=30)

        # Выводим JSON результат
        print(prediction.json())

        # Сохраняем результат предсказания в файл
        output_path = os.path.join(folder_path, f"prediction_{image_file}")
        prediction.save(output_path)


# Укажите путь к вашей папке с изображениями
folder_path = 'D:/2kurs/1234'
process_images_from_folder(folder_path)